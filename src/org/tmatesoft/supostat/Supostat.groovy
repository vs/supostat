package org.tmatesoft.supostat

@Grab(group = 'org.tmatesoft.svnkit', module = 'svnkit', version = '1.3.4')

def log = new LogCommand()
log.url = "svn://localhost/"
log.from = -1
log.to = -1
log.limit = 50000

log.run {def interestingDay, def count, def averageTime ->
  println "$interestingDay: $count commits every $averageTime seconds in average"
}
