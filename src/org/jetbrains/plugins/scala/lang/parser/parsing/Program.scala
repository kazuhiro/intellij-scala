package org.jetbrains.plugins.scala
package lang
package parser
package parsing

import builder.ScalaPsiBuilder
import org.jetbrains.plugins.scala.util.DebugPrint
import com.intellij.lang.PsiBuilder

/*
 * Program parses all content in scala source file
 */

object Program {

  def parse(builder: ScalaPsiBuilder): Int = {
    var parseState = 0
    // Debug print mode off
    DebugPrint.displayLog = false  

    if ( !builder.eof() ){
      parseState = CompilationUnit.parse(builder)
    }

    if (!builder.eof()) {
      while (!builder.eof()) {
        builder error ErrMsg("out.of.compilation.unit")
        builder.advanceLexer
      }
    }

    return parseState

  }
}