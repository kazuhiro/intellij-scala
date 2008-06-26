package org.jetbrains.plugins.scala.lang.resolve

import com.intellij.psi.scope._
import com.intellij.psi._
import com.intellij.openapi.util.Key
import psi.types.ScSubstitutor

import _root_.scala.collection.Set
import _root_.scala.collection.immutable.HashSet

class ResolveProcessor(override val kinds: Set[ResolveTargets], val name: String) extends BaseProcessor(kinds)
{
  def execute(element: PsiElement, state: ResolveState): Boolean = {
    val named = element.asInstanceOf[PsiNamedElement]
    val nameSet = state.get(ResolverEnv.nameKey)
    val elName = if (nameSet == null) named.getName else nameSet
    if (elName == name && kindMatches(element)) {
      var subst = state.get(ScSubstitutor.key)
      if (subst == null) subst = ScSubstitutor.empty
      candidates += new ScalaResolveResult(named, subst)
      return false //todo: for locals it is ok to terminate the walkup, later need more elaborate check
    }
    return true
  }

  override def getHint[T](hintClass: Class[T]): T =
    if (hintClass == classOf[NameHint] && name != "") new ScalaNameHint().asInstanceOf[T]
    else super.getHint(hintClass)

  class ScalaNameHint extends NameHint {
    def getName(state : ResolveState) = {
      val stateName = state.get(ResolverEnv.nameKey)
      if (stateName == null) name else stateName
    }
  }
}

import ResolveTargets._

object StdKinds {
  val stableQualRef = HashSet.empty[ResolveTargets] + PACKAGE + OBJECT + VAL
  val stableQualOrClass = stableQualRef + CLASS
  val stableImportSelector = HashSet.empty[ResolveTargets] + OBJECT + VAL + VAR + METHOD + PACKAGE + CLASS
  val stableClass = HashSet.empty[ResolveTargets] + CLASS

  val refExprLastRef = HashSet.empty[ResolveTargets] + OBJECT + VAL + VAR + METHOD
  val refExprQualRef = refExprLastRef + PACKAGE
}

object ResolverEnv {
  val nameKey: Key[String] = Key.create("ResolverEnv.nameKey")
}