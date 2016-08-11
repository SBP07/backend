package be.thomastoye.speelsysteem.models

import java.time.Instant

case class Attendance(day: Day.Id, shifts: Seq[Shift.Id]/*, registered: Option[Instant]*/)
