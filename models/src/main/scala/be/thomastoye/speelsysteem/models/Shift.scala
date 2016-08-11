package be.thomastoye.speelsysteem.models

import java.time.Instant

import be.thomastoye.speelsysteem.models.Shift.ShiftKind

object Day {
  type Id = String
}

case class Day(
  date: DayDate,
  shifts: Seq[Shift]
)

object Shift {
  type Id = String

  sealed trait ShiftKind { val mnemonic: String }

  /** Common shift kinds */
  object ShiftKind {
    case class Early() extends ShiftKind { override val mnemonic  = "VRO"}
    case class Morning() extends ShiftKind { override val mnemonic = "VM" }
    case class Noon() extends ShiftKind { override val mnemonic = "MID" }
    case class Afternoon() extends ShiftKind { override val mnemonic = "NM" }
    case class Evening() extends ShiftKind { override val mnemonic = "AV" }
    case class External() extends ShiftKind { override val mnemonic = "EXT" }
    case class Crew() extends ShiftKind { override val mnemonic = "LEI" }

    def apply(mnemonic: String) = mnemonic match {
      case "VRO" => Early()
      case "VM"  => Morning()
      case "MID" => Noon()
      case "NM"  => Afternoon()
      case "AV"  => Evening()
      case "EXT" => External()
      case "LEI" => Crew()
    }
  }
}

case class Shift(
  id: Shift.Id,
  price: Price,
  childrenCanBePresent: Boolean,
  crewCanBePresent: Boolean,
  kind: ShiftKind,
  location: Option[String],
  desciption: Option[String],
  startAndEnd: Option[StartAndEndTime]
)

case class StartAndEndTime(start: Instant, end: Instant)
