# Repro project for spring-framework#25768

See https://github.com/spring-projects/spring-framework/issues/25768
See https://github.com/HtmlUnit/htmlunit/issues/223

This project reproduces the issue with the behavior difference between `MockMvc` / `WebClient` test integrations.

The `WelcomeControllerTests` shows that in `WelcomeControllerTests#ajaxPost`, HtmlUnit is not parsing the form body to
add its inputs as request params. This is however done by HtmlUnit with a regular HTML form (see `WelcomeControllerTests#formPost`)
and a JQuery ajax call using a FormData object (see `WelcomeControllerTests#ajaxformDataPost`).

All tests but `WelcomeControllerTests#ajaxPost` are green, this shows the inconsistency. 

The project can be launched locally using `./gradlew bootRun`, and tests can be run with `./gradlew check`.
Template files are located under `src/main/resources/templates`.
