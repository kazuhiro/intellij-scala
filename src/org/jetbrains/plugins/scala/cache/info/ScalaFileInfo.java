/*
 * Copyright 2000-2006 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.plugins.scala.cache.info;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import com.intellij.psi.PsiClass;

/**
 * Main info about onew scala file
 * @author Ilya.Sergey
 */
public interface ScalaFileInfo extends Serializable {

  public long getFileTimestamp();

  public String getFileName();

  @NotNull
  public String getFileUrl();

  public String getFileDirectoryUrl();

  public String[] getClassNames();

  public void setClasses(PsiClass[] classes);

  public String toString();

  public boolean containsClass(String name);


}
