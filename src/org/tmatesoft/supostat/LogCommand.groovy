package org.tmatesoft.supostat

import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl
import org.tmatesoft.svn.core.wc.SVNClientManager
import org.tmatesoft.svn.core.wc.SVNRevision

/**
 * @author <a href="semen.vadishev@tmatesoft.com">Semen Vadishev</a>
 */
public class LogCommand {
  
  SVNURL url
  SVNRevision from
  SVNRevision to
  long limit

  void setUrl(String url) {
    this.url = SVNURL.parseURIEncoded(url)
  }

  void setFrom(long revision) {
    this.from = SVNRevision.create(revision);
  }
  
  void setTo(long revision) {
    this.to = SVNRevision.create(revision)
  }

  def run(Closure callback) {
    DAVRepositoryFactory.setup()
    FSRepositoryFactory.setup()
    SVNRepositoryFactoryImpl.setup()

    def handler = new LogEntryHandler(callback)
    def logClient = SVNClientManager.newInstance().logClient
    logClient.doLog url,
            [] as String[],
            SVNRevision.HEAD,
            from,
            to,
            false,
            false,
            false,
            limit,
            [] as String[],
            handler

    handler.done()
  }
}