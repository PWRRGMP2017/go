
package views.html

import play.templates._
import play.templates.TemplateMagic._

import play.api.templates._
import play.api.templates.PlayMagic._
import models._
import controllers._
import java.lang._
import java.util._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import play.api.i18n._
import play.core.j.PlayMagicForJava._
import play.mvc._
import play.data._
import play.api.data.Field
import play.mvc.Http.Context.Implicit._
import views.html._
/**/
object gameSettings extends BaseScalaTemplate[play.api.templates.HtmlFormat.Appendable,Format[play.api.templates.HtmlFormat.Appendable]](play.api.templates.HtmlFormat) with play.api.templates.Template1[String,play.api.templates.HtmlFormat.Appendable] {

    /**/
    def apply/*1.2*/(playerName: String):play.api.templates.HtmlFormat.Appendable = {
        _display_ {

Seq[Any](format.raw/*1.22*/("""

"""),_display_(Seq[Any](/*3.2*/main(playerName)/*3.18*/ {_display_(Seq[Any](format.raw/*3.20*/("""
    
    <div class="page-header">
        <h1>Game Settings</h1>
    </div>
    
    <div id="onError" class="alert-message error">
        <p>
            <strong>Oops!</strong> <span></span>
        </p>
    </div>

    <div id="onError2" style="display: none;" class="alert-message error">
        <p>
            <strong>Oops!</strong> <span></span>
        </p>
    </div>
    
    <div id="onChat" class="row">
    	
    </div>
    
    <script type="text/javascript" charset="utf-8" src=""""),_display_(Seq[Any](/*25.58*/routes/*25.64*/.Application.gameSettingsJS(playerName))),format.raw/*25.103*/(""""></script>
    
""")))})))}
    }
    
    def render(playerName:String): play.api.templates.HtmlFormat.Appendable = apply(playerName)
    
    def f:((String) => play.api.templates.HtmlFormat.Appendable) = (playerName) => apply(playerName)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Sat Jan 28 23:04:13 CET 2017
                    SOURCE: C:/Users/simba/Dropbox/Private/PWr/3sem/tp/lab/4/go/go-root/go-webapp/app/views/gameSettings.scala.html
                    HASH: 271750768a25294057811bfe8d4324d47ffa57a8
                    MATRIX: 781->1|895->21|932->24|956->40|995->42|1529->540|1544->546|1606->585
                    LINES: 26->1|29->1|31->3|31->3|31->3|53->25|53->25|53->25
                    -- GENERATED --
                */
            