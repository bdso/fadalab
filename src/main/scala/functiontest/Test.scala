package functiontest

import java.util.UUID

object Test {
  def main(args: Array[String]): Unit = {
    val to_encode = "Hello World"
    val uuid_bytes = UUID.nameUUIDFromBytes(to_encode.trim().getBytes())
    printf("String to encode: %s\n", to_encode)
    printf("UUID: %s\n", uuid_bytes.toString)
  }
}
