package org.tmatesoft.supostat

import org.tmatesoft.svn.core.ISVNLogEntryHandler
import org.tmatesoft.svn.core.SVNLogEntry

/**
 * @author <a href="semen.vadishev@tmatesoft.com">Semen Vadishev</a>
 */
class LogEntryHandler implements ISVNLogEntryHandler {

  def callback
  SVNLogEntry previousEntry
  int commitsCount
  List commitTimes

  LogEntryHandler(def callback) {
    this.callback = callback
    reset()
  }

  void handleLogEntry(SVNLogEntry entry) {
    if (entry == null || entry.date == null) {
      return
    }
    if (previousEntry == null) {
      commitsCount++
      commitTimes += entry.date.time
      previousEntry = entry
      return
    }

    if (ofTheSameDay(entry)) {
      update(entry)
    } else {
      reportDayStat()
      reset()
    }
    previousEntry = entry
  }

  void done() {
    reportDayStat()
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
    commitTimes += entry.date.time
  }

  def reportDayStat() {
    callback(dayStat)
  }

  def getDayStat() {
    DayStat stat = new DayStat()
    stat.date = interestingDay
    stat.count = commitsCount
    stat.min = minimalTimeDelta
    stat.max = maximalTimeDelta
    stat.avg = averageTimeDelta
    return stat
  }

  def getInterestingDay() {
    assert previousEntry != null
    previousEntry.date.format('yyyy.MM.dd')
  }

  List<Long> getCommitTimeDeltas() {
    if (commitTimes.size() <= 1) {
      return []
    }
    commitTimes = commitTimes.sort()
    def commitTimeDeltas = []
    for (int i = 0; i < commitTimes.size() - 1; i++) {
      long delta = commitTimes[i + 1] - commitTimes[i]
      commitTimeDeltas += Math.abs(delta)
    }
    return commitTimeDeltas
  }

  long getAverageTimeDelta() {
    def deltas = commitTimeDeltas
    if (deltas.empty) {
      return 0
    }
    def averageTimeDeltaInMillis =  deltas.sum() / deltas.size()
    return (averageTimeDeltaInMillis / 1000) as int
  }

  long getMinimalTimeDelta() {
    if (commitTimeDeltas.empty) {
      return 0
    }
    return (commitTimeDeltas.min() / 1000) as int
  }

  long getMaximalTimeDelta() {
    if (commitTimeDeltas.empty) {
      return 0
    }
    return (commitTimeDeltas.max() / 1000) as int
  }

  def reset() {
    previousEntry = null
    commitsCount = 0
    commitTimes = []
  }
}
