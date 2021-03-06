package org.jetbrains.plugins.scala
package lang
package resolve

import com.intellij.psi.impl.source.resolve.ResolveCache
import processor._
import psi.api.base.patterns.{ScConstructorPattern, ScInfixPattern}
import psi.api.toplevel.imports.{ScImportExpr, ScImportSelector}
import psi.types.Compatibility.Expression
import psi.api.expr.{ScSuperReference, ScThisReference}
import psi.api.base.types.{ScInfixTypeElement, ScSimpleTypeElement, ScParameterizedTypeElement}
import psi.api.statements.{ScMacroDefinition, ScTypeAlias}
import com.intellij.psi.util.PsiTreeUtil
import psi.ScalaPsiUtil
import psi.api.toplevel.ScTypeBoundsOwner
import psi.api.statements.params.ScTypeParam
import psi.api.base.{ScMethodLike, ScStableCodeReferenceElement}
import com.intellij.psi.PsiFile
import org.jetbrains.plugins.scala.lang.psi.api.ScalaFile

class StableCodeReferenceElementResolver(reference: ResolvableStableCodeReferenceElement, shapeResolve: Boolean,
                                          allConstructorResults: Boolean, noConstructorResolve: Boolean)
        extends ResolveCache.PolyVariantResolver[ScStableCodeReferenceElement] {
  def resolve(ref: ScStableCodeReferenceElement, incomplete: Boolean) = {
    val kinds = ref.getKinds(false)

    val proc = if (ref.isConstructorReference && !noConstructorResolve) {
      val constr = ref.getConstructor.get
      val typeArgs = constr.typeArgList.map(_.typeArgs).getOrElse(Seq())
      val effectiveArgs = constr.arguments.toList.map(_.exprs.map(new Expression(_))) match {
        case List() => List(List())
        case x => x
      }
      new ConstructorResolveProcessor(ref, ref.refName, effectiveArgs, typeArgs, kinds, shapeResolve, allConstructorResults)
    } else ref.getContext match {
      //last ref may import many elements with the same name
      case e: ScImportExpr if (e.selectorSet == None && !e.singleWildcard) =>
        new CollectAllForImportProcessor(kinds, ref, reference.refName)
      case e: ScImportExpr if e.singleWildcard => new ResolveProcessor(kinds, ref, reference.refName)
      case _: ScImportSelector => new CollectAllForImportProcessor(kinds, ref, reference.refName)
      case constr: ScConstructorPattern =>
        new ExtractorResolveProcessor(ref, reference.refName, kinds, constr.expectedType)
      case infix: ScInfixPattern => new ExtractorResolveProcessor(ref, reference.refName, kinds, infix.expectedType)
      case _ => new ResolveProcessor(kinds, ref, reference.refName)
    }

    reference.doResolve(ref, proc)
  }
}
