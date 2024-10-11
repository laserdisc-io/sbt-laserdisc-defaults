# see https://www.scala-sbt.org/1.x/docs/Testing-sbt-plugins.html for help with syntax

# reminder: `+` at the beginning of an sbt task triggers cross-compilation (aka the scenarios will run for scala 2.13 and 3.x)
# but, relevant for this test '->' expects failure, '>' expects success