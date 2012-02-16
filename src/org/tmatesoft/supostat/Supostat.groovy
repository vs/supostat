package org.tmatesoft.supostat

@Grab(group = 'org.tmatesoft.svnkit', module = 'svnkit', version = '1.3.4')

def log = new LogCommand()
log.url = "svn://localhost/"
log.from = -1
log.to = -1
log.limit = 10000

println "Repository: $log.url"
log.run {DayStat stat ->
  if (stat.count > 1) {
    println "$stat.date: $stat.count commits (min: $stat.min sec; max: $stat.max sec; avg: $stat.avg sec)"
  }
}
