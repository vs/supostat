package org.tmatesoft.supostat

import org.tmatesoft.svn.core.ISVNLogEntryHandler
import org.tmatesoft.svn.core.SVNLogEntry

/**
 * @author <a href="semen.vadishev@tmatesoft.com">Semen Vadishev</a>
 */
class LogEntryHandler implements ISVNLogEntryHandler {

  def callback
  SVNLogEntry previousEntry
  def commitsCount
  def periods

  LogEntryHandler(def callback) {
    this.callback = callback
    reset()
  }

  void handleLogEntry(SVNLogEntry entry) {
    if (entry == null || entry.date == null) {
      return
    }
    if (previousEntry == null) {
      previousEntry = entry
      commitsCount++
      return
    }

    if (ofTheSameDay(entry)) {
      update(entry)
    } else {
      reportDay()
      reset()
    }
    previousEntry = entry
  }

  void done() {
    reportDay()
    reset()
  }

  private boolean ofTheSameDay(SVNLogEntry entry) {
    assert previousEntry != null
    return previousEntry.date.year == entry.date.year &&
            previousEntry.date.month == entry.date.month &&
            previousEntry.date.day == entry.date.day
  }

  def update(SVNLogEntry entry) {
    commitsCount++
    periods.add(Math.abs(entry.date.time - previousEntry.date.time))
  }

  def reportDay() {
    callback(interestingDay, commitsCount, averagePeriod)
  }

  def getInterestingDay() {
    assert previousEntry != null
    previousEntry.date.format('yyyy.MM.dd')
  }

  def getAveragePeriod() {
    if (periods.empty) {
      return 0
    }
    float sum = 0
    periods.each {sum += it}
    def averagePeriodInMillis =  sum / periods.size()
    return (averagePeriodInMillis / 1000) as int
  }

  def reset() {
    previousEntry = null
    commitsCount = 0
    periods = []
  }
}
