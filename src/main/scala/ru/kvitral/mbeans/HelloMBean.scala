package ru.kvitral.mbeans

trait DefaultMBean

trait HelloMBean extends DefaultMBean {

  def getName: String

  def setName(str: String): Unit

}

class Hello extends HelloMBean {
  var name = "Scala"

  override def getName: String = name

  override def setName(str: String): Unit = name = str
}
