package functiontest

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object Test {
  def main(args: Array[String]): Unit = {

    //    val to_encode = "Hello World"
    //    val uuid_bytes = UUID.nameUUIDFromBytes(to_encode.trim().getBytes())
    //    printf("String to encode: %s\n", to_encode)
    //    printf("UUID: %s\n", uuid_bytes.toString)
    //    val date = "2016-12-29 09:54:00"
    //    val date = LocalDateTime.now()
    //    convertTime2(date.toString)

    val d1 = "2017-01-01 11:09:00"
    val d2 = "2017-01-03 11:09:00"

    val days = comTimeActiveUser(d1, d2)
    System.out.println(days)
  }

  private def comTimeActiveUser(date1: String, date2: String) = {
    val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val localDate1 = LocalDateTime.parse(date1, fmt)
    val localDate2 = LocalDateTime.parse(date2, fmt)
    val days = ChronoUnit.DAYS.between(localDate1, localDate2)
    days
  }

  private def convertTime1(date: String) = {
    import java.time.format.DateTimeFormatter
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
    val formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val localDate = LocalDateTime.parse(date, formatter)

    System.out.println(localDate)

    System.out.println(formatter2.format(localDate))
  }

  private def convertTime2(date: String) = {
    val fmt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
    val fmt2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val localDate = LocalDateTime.parse(date, fmt1)
    System.out.println(localDate)
    System.out.println(fmt2.format(localDate))
  }
}
