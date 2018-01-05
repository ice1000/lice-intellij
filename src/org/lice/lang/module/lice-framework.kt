package org.lice.lang.module

import com.intellij.framework.FrameworkTypeEx
import com.intellij.framework.addSupport.FrameworkSupportInModuleProvider
import org.lice.lang.LICE_BIG_ICON
import org.lice.lang.LICE_NAME

class LiceFrameworkType : FrameworkTypeEx(LICE_NAME) {
	override fun getIcon() = LICE_BIG_ICON
	override fun getPresentableName(): String {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun createProvider(): FrameworkSupportInModuleProvider {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

}