package poc
import org.scalatest.Assertions

trait EitherValues {
  self: Assertions =>

  implicit class EitherOps[L, R](either: Either[L, R]) {
    def leftValue: L = either.swap.getOrElse(self.fail("Either was not Left"))

    def rightValue: R = either.getOrElse(self.fail("Either was not Right"))
  }
}
