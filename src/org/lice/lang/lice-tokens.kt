package org.lice.lang

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

class LiceTokenType(debugName: String) : IElementType(debugName, LiceLanguage) {
	companion object {
		@JvmField val WHITE_SPACE: IElementType = TokenType.WHITE_SPACE
		@JvmField val COMMENT = LiceTokenType("COMMENT")
		@JvmField val LB = LiceTokenType("LEFT_BRACKET")
		@JvmField val RB = LiceTokenType("RIGHT_BRACKET")
		@JvmField val SYM = LiceTokenType("SYMBOL")
		@JvmField val STR = LiceTokenType("STRING")
		@JvmField val COMMENTS = TokenSet.create(COMMENT)
		@JvmField val WHITE_SPACES = TokenSet.create(WHITE_SPACE)
		@JvmField val STRINGS = TokenSet.create(STR)
	}

	override fun toString() = "Lice: ${super.toString()}"
}

