/**
 * Created by ice1000 on 2017/3/30.
 *
 * @author ice1000
 */
package org.lice.tools

import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBScrollPane
import org.apache.commons.lang.StringUtils
import org.lice.compiler.model.StringLeafNode
import org.lice.compiler.model.StringMiddleNode
import org.lice.compiler.model.StringNode
import org.lice.compiler.parse.buildNode
import org.lice.lang.LiceInfo
import org.lice.repl.VERSION_CODE
import java.awt.BorderLayout
import java.awt.Dimension
import java.io.File
import java.time.LocalDate
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode

typealias UINode = DefaultMutableTreeNode

/**
 * map the ast
 */
private fun mapAst2Display(
		node: StringNode,
		viewRoot: UINode
): UINode = when (node) {
	is StringLeafNode -> UINode(node)
	is StringMiddleNode -> viewRoot.apply {
		node.list.subList(1, node.list.size).forEach { add(mapAst2Display(it, UINode(it))) }
	}
	else -> UINode("null")
}

/**
 * map the ast
 */
private fun mapDisplay2Ast(
		node: UINode,
		gen: StringBuilder,
		numOfIndents: Int = 0) {
	if (numOfIndents == 0) gen.append("\n")
	when {
		node.isLeaf -> gen.append(" ").append(node.userObject.toString()).append("")
		else -> {
			gen.append("\n")
					.append(StringUtils.repeat("  ", numOfIndents))
					.append("(")
					.append(node.userObject.toString())
			node.children().toList().forEach {
				mapDisplay2Ast(
						it as UINode,
						gen,
						numOfIndents + 1
				)
			}
			gen.append(")")
		}
	}
}

private fun createTreeRootFromFile(file: File): UINode {
	val ast = buildNode(file.readText())
	return mapAst2Display(ast, UINode(ast))
}

/**
 * @author ice1000
 */
fun displaySyntaxTree(file: File) {
	val frame = JFrame("Lice Syntax Tree $VERSION_CODE")
	frame.layout = BorderLayout()
	frame.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
	frame.setLocation(80, 80)
	frame.setSize(480, 480)
	fun File.neighbour() = "$parent/$name-edited-${System.currentTimeMillis()}.lice"
	try {
		val root = createTreeRootFromFile(file)
		frame.add(
				JBScrollPane(JTree(root).apply { isEditable = true }),
				BorderLayout.CENTER
		)
		val button = JButton("Export Lice Code")
		button.addActionListener {
			val sb = StringBuilder(""";
; Generated by IntelliJ Lice Plugin on ${LocalDate.now()}
; See: https://github.com/lice-lang/lice-intellij
;""")
			root.children().toList().forEach { mapDisplay2Ast(it as UINode, sb) }
			val name = file.neighbour()
			File(name)
					.apply { if (!exists()) createNewFile() }
					.writeText(sb.toString())
			Messages.showMessageDialog("""Successfully Export to:
$name""", "Export Lice Code", LiceInfo.LICE_ICON)
			frame.add(button, BorderLayout.SOUTH)
		}
	} catch (e: RuntimeException) {
		val label = JTextArea(e.message)
		e.stackTrace.forEach { label.append("\t$it\n") }
		label.isEditable = false
		label.preferredSize = Dimension(600, 600)
		frame.add(label, BorderLayout.CENTER)
	}
	frame.isVisible = true
}