package poc

import java.io.File
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._

class CSVTest extends UnitTest with EitherValues {

  "実csvとheaderの順番が異なっていてもデコードできる" in {
    case class Info(bool: Boolean, num: Int, str: String)

    val file = new File("src/test/resources/simple.csv")

    implicit val headerDecoder = HeaderDecoder.decoder("bool", "num", "str")(Info.apply _)
    val reader = file.asCsvReader[Info](rfc.withHeader)
    val firstLine = reader.next()
    val secondLine = reader.next()

    assert(firstLine.rightValue === Info(true, 1, "one" ))
    assert(secondLine.rightValue === Info(false, 2, "two"))

  }

  "途中の空行はTypeError" in {
    val file = new File("src/test/resources/emptyLine.csv")
    val reader = file.asCsvReader[(String, Int, Boolean)](rfc.withHeader)
    val firstLine = reader.next()
    reader.next().leftValue
    val threeLine = reader.next()

    assert(firstLine.rightValue === ("one", 1, true))
    assert(threeLine.rightValue === ("three", 3, false))
  }

  "最終行が空でもエラーにならない" in {
    val file = new File("src/test/resources/blankFinalLine.csv")
    val reader = file.asCsvReader[(String, Int, Boolean)](rfc.withHeader)
    val firstLine = reader.next()
    val secondLine = reader.next()

    assert(firstLine.rightValue === ("one", 1, true))
    assert(secondLine.rightValue === ("two", 2, false))
  }

  "エスケース文字が適用される" in {
    val file = new File("src/test/resources/escape.csv")
    val reader = file.asCsvReader[(String, Int, Boolean)](rfc.withHeader)
    val firstLine = reader.next()
    val secondLine = reader.next()

    assert(firstLine.rightValue === ("\"one", 1, true))
    assert(secondLine.rightValue === ("two\"", 2, false))
  }

  "項目内改行でも解析できる" in {
    val file = new File("src/test/resources/lineBreak.csv")
    val reader = file.asCsvReader[(String, String, String)](rfc)
    val firstLine = reader.next()
    val secondLine = reader.next()

    assert(firstLine.rightValue === ("a", "b\nb", "c"))
    assert(secondLine.rightValue === ("\nd", "e", "f"))
  }
  "optionalに変換できる" in {
    case class Info(bool: Option[String], num: Option[Int], str: Option[Boolean])

    val file = new File("src/test/resources/optional.csv")
    val reader = file.asCsvReader[Info](rfc.withHeader)
    val firstLine = reader.next()
    val secondLine = reader.next()
    val thirdLine = reader.next()

    assert(firstLine.rightValue === Info(None, Some(1), Some(true)))
    assert(secondLine.rightValue === Info(Some("two"), None, Some(false)))
    assert(thirdLine.rightValue === Info(Some("three"), Some(3), None))
  }
  "文字列数字、文字列Boolに変換" in {
    val file = new File("src/test/resources/simple.csv")
    val reader = file.asCsvReader[(String, String, String)](rfc.withHeader)
    val firstLine = reader.next()
    val secondLine = reader.next()

    assert(firstLine.rightValue === ("one", "1", "true"))
    assert(secondLine.rightValue === ("two", "2", "false"))
  }
  "文字列はOptional指定でないと空文字となる" in {
    val file = new File("src/test/resources/optional.csv")
    val reader = file.asCsvReader[(String, Int, Boolean)](rfc.withHeader)
    val firstLine = reader.next()
    val secondLine = reader.next()

    assert(firstLine.rightValue === ("", 1, true))
    secondLine.leftValue
  }
  "列が超過していても順番に解析は可能" in {
    val file = new File("src/test/resources/exceedRow.csv")
    val reader = file.asCsvReader[(String, Int, Boolean)](rfc.withHeader)
    val firstLine = reader.next()
    val secondLine = reader.next()

    assert(firstLine.rightValue === ("one", 1, true))
    assert(secondLine.rightValue === ("two", 2, false))
  }
  "tsv形式で解析は可能" in {
    val file = new File("src/test/resources/simple.tsv")
    val reader = file.asCsvReader[(String, Int, Boolean)](rfc.withHeader.withCellSeparator('\t'))
    val firstLine = reader.next()
    val secondLine = reader.next()

    assert(firstLine.rightValue === ("one", 1, true))
    assert(secondLine.rightValue === ("two", 2, false))
  }
  "空ファイルの場合、Iteratorは空" in {
    val file = new File("src/test/resources/empty.csv")
    val reader = file.asCsvReader[(String, Int, Boolean)](rfc)
    assert(reader.isEmpty === true)
  }
  "ヘッダーのみ場合、Iteratorは空" in {
    val file = new File("src/test/resources/headerOnly.csv")
    val reader = file.asCsvReader[(String, Int, Boolean)](rfc.withHeader)
    assert(reader.isEmpty === true)
  }
}
