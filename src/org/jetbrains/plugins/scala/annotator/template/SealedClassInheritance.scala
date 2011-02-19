package org.jetbrains.plugins.scala.annotator.template

import org.jetbrains.plugins.scala.annotator.AnnotatorPart
import com.intellij.lang.annotation.AnnotationHolder
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScNewTemplateDefinition
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef.{ScTypeDefinition, ScTemplateDefinition}

/**
 * Pavel Fatin
 */

object SealedClassInheritance extends AnnotatorPart[ScTemplateDefinition] {
  def kind = classOf[ScTemplateDefinition]

  def annotate(definition: ScTemplateDefinition, holder: AnnotationHolder, advanced: Boolean) {
    val newInstance = definition.isInstanceOf[ScNewTemplateDefinition]
    val hasBody = definition.extendsBlock.templateBody.isDefined

    if (newInstance && !hasBody) return

    definition.refs.foreach {
      case (refElement, Some(psiClass: ScTypeDefinition)) if psiClass.getModifierList.hasModifierProperty("sealed") &&
              psiClass.getContainingFile != refElement.getContainingFile => {
        holder.createErrorAnnotation(refElement,
          "Illegal inheritance from sealed %s %s".format(kindOf(psiClass).toLowerCase, psiClass.getName))
      }
      case _ =>
    }
  }
}