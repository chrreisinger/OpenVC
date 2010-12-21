/*
 *     OpenVC, an open source VHDL compiler/simulator
 *     Copyright (C) 2010  Christian Reisinger
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.jku.ssw.openvs

object Simulator {

  import java.net.URLClassLoader

  def crateClassLoader(parentClassLoader: ClassLoader, directory: String, jars: Seq[String]): URLClassLoader = {
    import java.io.File
    val builder = scala.collection.mutable.ArrayBuilder.make[java.net.URL]
    builder.sizeHint(1 + jars.size)
    builder += new File(directory + File.separator).toURI.toURL
    builder ++= jars.map(new File(_).toURI.toURL)
    new URLClassLoader(builder.result.toArray, parentClassLoader)
  }

  def runClass(parentClassLoader: ClassLoader, directory: String, name: String, method: String, jars: Seq[String]) {
    val loader = crateClassLoader(parentClassLoader, directory, jars)
    val cl = loader.loadClass(name)
    val o = cl.newInstance
    val nm = cl.getMethod(method)
    nm.setAccessible(true)
    nm.invoke(o)
  }

  private[this] class MyClassLoader(classLoader: ClassLoader) extends ClassLoader(classLoader) {
    def loadAndResolveClass(className: String, resolve: Boolean) = super.loadClass(className, resolve)
  }

  def loadFiles(parentClassLoader: ClassLoader, directory: String, files: Seq[String], jars: Seq[String]) {
    val classLoader = new MyClassLoader(crateClassLoader(parentClassLoader, directory, jars))
    files.foreach(className => classLoader.loadAndResolveClass(className, true))
  }
}