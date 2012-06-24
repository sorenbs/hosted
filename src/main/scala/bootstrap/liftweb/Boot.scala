package bootstrap.liftweb

import net.liftweb._
import http.Req.NilPath
import util._
import Helpers._
import common._
import http._
import sitemap._
import Loc._
import com.mongodb.ServerAddress
import net.liftweb.mongodb.MongoDB
import net.liftweb.mongodb.DefaultMongoIdentifier
import com.mongodb.Mongo
import com.foursquare.rogue.Rogue._
import code.model.CraftCode
import org.joda.time.DateTime

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    
    
    val srvr = new ServerAddress(
	  Props.get("mongodb.url") openOr "craftybuilder.com",
	  Props.getInt("mongodb.port") openOr 27017)
	
    MongoDB.defineDbAuth(
        DefaultMongoIdentifier, 
        new Mongo(srvr), 
        Props.get("mongodb.db") openOr "craftybuilder",
        Props.get("mongodb.user") openOr "CraftyBuilder",
        Props.get("mongodb.pass") openOr System.getenv("builder.mongodb.pass")) 
    
    

    // where to search snippet
    LiftRules.addToPackages("code")

    // Build SiteMap
    val entries = List(
      Menu.i("Home") / "index", // the simple way to declare a menu
      Menu.i("Editor") / "editor",

      // more complex because this menu allows anything in the
      // /static path to be visible
      Menu(Loc("Static", Link(List("static"), true, "/static/index"), 
	       "Static Content")))

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMap(SiteMap(entries:_*))

    // Use jQuery 1.4
    LiftRules.jsArtifacts = net.liftweb.http.js.jquery.JQuery14Artifacts

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    
    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

// Use HTML5 for rendering
LiftRules.htmlProperties.default.set((r: Req) =>
  new Html5Properties(r.userAgent))  

  }
  ///
  // /api/*
  ///
  LiftRules.dispatch.append(code.lib.CraftRest)

  ///
  // /craft/x -> /craft/x/0
  ///
  LiftRules.dispatch.append {
    case Req("craft" :: id :: Nil, _, _) =>
      () => S.redirectTo("/craft/" + id + "/0")
  }

  ///
  // / -> /craft/x/0
  ///
  LiftRules.dispatch.append {

    case Req("index" :: Nil, _, _) =>
      () =>
        {
          val nextId = code.model.Counter
            .where(_.name eqs "Craft")
            .findAndModify(_.counter inc 1)
            .updateOne(returnNew = true)
            .asInstanceOf[scala.Some[code.model.Counter]].get.counter.value
          
          val secretId = StringHelpers.randomString(16);


          code.model.Craft.createRecord
          	.shortId(nextId)
          	.secretId(secretId)
          	.code(CraftCode.createRecord
          	    .code("")
          	    .comment("")
          	    .created(new DateTime().toGregorianCalendar()) :: Nil)
          	.save
          S.redirectTo("/craft/" + secretId + "/0")
        }
  }

  ///
  // /craft/x/y -> /index
  ///
  LiftRules.statelessRewrite.prepend(NamedPF("CraftRewrite") {
    case RewriteRequest( ParsePath("craft" :: id :: version :: Nil, _, _, _), _, _ ) =>
      RewriteResponse( "editor" :: Nil, Map("simpleId" -> id, "version" -> version))
  })
}
