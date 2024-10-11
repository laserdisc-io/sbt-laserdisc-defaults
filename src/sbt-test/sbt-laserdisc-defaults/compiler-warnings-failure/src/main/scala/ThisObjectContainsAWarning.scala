object ThisObjectContainsAWarning {

  def apply(input: List[Int]): String = input match {
    case List(1234) => "whatever"
    // omitting other cases to trigger "match may not be exhaustive" warning
  }

}
