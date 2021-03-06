package org.lice.lang.psi

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import org.intellij.lang.annotations.Language
import org.lice.core.SymbolList
import org.lice.lang.LiceBundle
import org.lice.lang.LiceSyntaxHighlighter
import org.lice.lang.editing.LiceSymbols
import java.util.regex.Pattern

class LiceSymbolsExtractingAnnotator : Annotator {
	companion object RegExes {
		@Language("RegExp") private
		const val SYMBOL_CHAR = "[a-zA-Z!@\$^&_:=<|>?.\\\\+\\-~*/%\\[\\]#{}]"

		@Language("RegExp") private
		const val SYMBOL = "$SYMBOL_CHAR($SYMBOL_CHAR|[0-9])*"

		val SYMBOL_REGEX = Pattern.compile(SYMBOL).toRegex()

		@get:Synchronized
		val javaDefinitions = mutableSetOf<PsiLiteralExpression>()
	}

	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		val methodCall = element as? PsiMethodCallExpression ?: return
		val callee = methodCall.methodExpression.children.firstOrNull { it is PsiExpression } as? PsiExpression ?: return
		val calleeType = callee.type ?: return
		if (calleeType.isValid && calleeType.canonicalText == SymbolList::class.java.canonicalName) {
			val method = methodCall.methodExpression.children.firstOrNull { it is PsiIdentifier } ?: return
			val possibleString = methodCall.argumentList.children
					.firstOrNull { it is PsiLiteralExpression } as? PsiLiteralExpression ?: return
			val str = possibleString.value as? String ?: return
			val isFunc = "Function" in method.text
			val isVar = "Variable" in method.text
			javaDefinitions += possibleString
			if ((isVar or isFunc) and !SYMBOL_REGEX.matches(str)) {
				holder.createWarningAnnotation(
						TextRange(possibleString.textRange.startOffset + 1, possibleString.textRange.endOffset - 1),
						LiceBundle.message("lice.lint.java.invalid-symbol"))
			}
			if (isFunc) {
				holder.createInfoAnnotation(
						TextRange(possibleString.textRange.startOffset + 1, possibleString.textRange.endOffset - 1),
						LiceBundle.message("lice.lint.java.func-def"))
						.textAttributes = LiceSyntaxHighlighter.FUNCTION_DEFINITION
			} else if (isVar) {
				if (possibleString.value is String) holder.createInfoAnnotation(
						TextRange(possibleString.textRange.startOffset + 1, possibleString.textRange.endOffset - 1),
						LiceBundle.message("lice.lint.java.var-def"))
						.textAttributes = LiceSyntaxHighlighter.VARIABLE_DEFINITION
			}
			LiceSymbols.checkName(possibleString, holder, str)
		}
	}
}
