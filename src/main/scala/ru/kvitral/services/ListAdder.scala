package ru.kvitral.services

class ListAdder(capaticy: Int) {

  var initList = List.fill(capaticy)(1)


  def properAddToList(elem: Int): List[Int] = {
    initList = elem +: initList
    initList
  }

  def slowerAddToList(elem: Int): List[Int] = {
    initList = initList :+ elem
    initList
  }

}
