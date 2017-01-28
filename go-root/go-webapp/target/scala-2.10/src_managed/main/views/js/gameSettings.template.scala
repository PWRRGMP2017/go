
package views.js

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
import views.js._
/**/
object gameSettings extends BaseScalaTemplate[play.api.templates.JavaScriptFormat.Appendable,Format[play.api.templates.JavaScriptFormat.Appendable]](play.api.templates.JavaScriptFormat) with play.api.templates.Template1[String,play.api.templates.JavaScriptFormat.Appendable] {

    /**/
    def apply/*1.2*/(playerName: String):play.api.templates.JavaScriptFormat.Appendable = {
        _display_ {

Seq[Any](format.raw/*1.22*/("""

$(function() """),format.raw/*3.14*/("""{"""),format.raw/*3.15*/("""
    var socket = new WebSocket(""""),_display_(Seq[Any](/*4.34*/routes/*4.40*/.Application.joinPlayerRoom(playerName).webSocketURL(request))),format.raw/*4.101*/("""");

    var receiveEvent = function(event) """),format.raw/*6.40*/("""{"""),format.raw/*6.41*/("""
        var data = JSON.parse(event.data);

        // Handle errors
        if(data.error) """),format.raw/*10.24*/("""{"""),format.raw/*10.25*/("""
            socket.close();
            $("#onError span").text(data.error);
            $("#onError").slideDown(1000);
            return;
        """),format.raw/*15.9*/("""}"""),format.raw/*15.10*/("""

        if (data.type === "error") """),format.raw/*17.36*/("""{"""),format.raw/*17.37*/("""
            $("#onError span").text(data.message);
            $("#onError").slideDown(1000);
        """),format.raw/*20.9*/("""}"""),format.raw/*20.10*/("""
    """),format.raw/*21.5*/("""}"""),format.raw/*21.6*/("""

    var closeEvent = function(event) """),format.raw/*23.38*/("""{"""),format.raw/*23.39*/("""
        socket.close();
        $("#onError2 span").text("Server closed the connection. The webpage will be refreshed shortly.");
        $("#onError2").slideDown(1000);
        setTimeout(function () """),format.raw/*27.32*/("""{"""),format.raw/*27.33*/("""
            window.location.href = '"""),_display_(Seq[Any](/*28.38*/routes/*28.44*/.Application.index())),format.raw/*28.64*/("""';
        """),format.raw/*29.9*/("""}"""),format.raw/*29.10*/(""", 5000);
    """),format.raw/*30.5*/("""}"""),format.raw/*30.6*/("""

    socket.onmessage = receiveEvent;
    socket.onclose = closeEvent;
"""),format.raw/*34.1*/("""}"""),format.raw/*34.2*/(""")
"""))}
    }
    
    def render(playerName:String): play.api.templates.JavaScriptFormat.Appendable = apply(playerName)
    
    def f:((String) => play.api.templates.JavaScriptFormat.Appendable) = (playerName) => apply(playerName)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Sat Jan 28 23:36:11 CET 2017
                    SOURCE: C:/Users/simba/Dropbox/Private/PWr/3sem/tp/lab/4/go/go-root/go-webapp/app/views/gameSettings.scala.js
                    HASH: 8b819f27d29ef895169c512552418a376b393ae3
                    MATRIX: 801->1|921->21|963->36|991->37|1060->71|1074->77|1157->138|1228->182|1256->183|1377->276|1406->277|1582->426|1611->427|1676->464|1705->465|1835->568|1864->569|1896->574|1924->575|1991->614|2020->615|2250->817|2279->818|2353->856|2368->862|2410->882|2448->893|2477->894|2517->907|2545->908|2644->980|2672->981
                    LINES: 26->1|29->1|31->3|31->3|32->4|32->4|32->4|34->6|34->6|38->10|38->10|43->15|43->15|45->17|45->17|48->20|48->20|49->21|49->21|51->23|51->23|55->27|55->27|56->28|56->28|56->28|57->29|57->29|58->30|58->30|62->34|62->34
                    -- GENERATED --
                */
            