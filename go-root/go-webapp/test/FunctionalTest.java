public class FunctionalTest {
//    @Test
//    public void sendJavaScript() {
//        running(fakeApplication(), new Runnable() {
//            @Override
//            public void run() {
//                Result result = callAction(controllers.routes.ref.Application.chatRoomJs("'"));
//                assertThat(status(result)).isEqualTo(OK);
//                assertThat(contentType(result)).isEqualTo("text/javascript");
//            }
//        });
//    }
//
//    @Test
//    public void resistToXSS() {
//        running(fakeApplication(), new Runnable() {
//            @Override
//            public void run() {
//                Result result = callAction(controllers.routes.ref.Application.chatRoomJs("'"));
//                assertThat(contentAsString(result)).contains("if(data.user == '\\'')");
//            }
//        });
//    }
}
