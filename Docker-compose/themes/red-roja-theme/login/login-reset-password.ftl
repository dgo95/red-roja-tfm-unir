<#-- login-reset-password.ftl -->

<#assign langTag = locale.currentLanguageTag />
<#if langTag == "ca" || langTag == "ca-ES">
  <#assign bgUrl = url.resourcesPath + "/img/1500x500-ca_ES.jpg" />
<#else>
  <#assign bgUrl = url.resourcesPath + "/img/1500x500-es_ES.jpg" />
</#if>

<style>
  :root { --kc-bg-url: url("${bgUrl}"); }
</style>

<#import "template.ftl" as layout>
<#import "social-providers.ftl" as identityProviders>

<@layout.registrationLayout
    displayMessage = !messagesPerField.existsError('username')
    displayInfo    = false
  ; section>

  <#-- HEADER: usamos la clave correcta -->
  <#if section == "header">
    ${msg("emailForgotTitle")}

  <#-- FORM -->
  <#elseif section == "form">
    <div class="container-fluid ps-md-0 h-100">
      <div class="row g-0 h-100">
        <div class="col-12 d-flex align-items-center justify-content-center h-100">
          <div class="w-100 px-3 px-md-5" style="max-width:480px;">

            <form
              id="kc-reset-password-form"
              class="${properties.kcFormClass!}"
              action="${url.loginAction}"
              method="post"
              novalidate>

              <!-- Username/Email -->
              <div class="form-floating mb-3">
                <input
                  type="text"
                  name="username"
                  id="inputUsername"
                  autocomplete="username"
                  class="form-control <#if messagesPerField.existsError('username')>is-invalid</#if>"
                  placeholder="${msg("username")}"
                  value="${login.username!''}" />
                <label for="inputUsername">${msg("username")}</label>
                <#if messagesPerField.existsError('username')>
                  <div class="invalid-feedback">
                    <#list messagesPerField.get('username') as m>${m}</#list>
                  </div>
                </#if>
              </div>

                <!-- Botón de enviar enlace de restauración -->
			  <div class="d-grid">
				<button
				  type="submit"
				  id="kc-reset"
				  class="btn btn-lg btn-danger btn-login text-uppercase fw-bold mb-2">
				  ${msg("doSubmit")}
				</button>
				<button
				  type="button"
				  onclick="location.href='${url.loginUrl}'"
				  class="btn btn-lg btn-secondary btn-login text-uppercase fw-bold">
				  ${kcSanitize(msg("backToLogin"))?no_esc}
				</button>
			  </div>
            </form>

          </div>
        </div>
      </div>
    </div>

  <#-- INFO tras envío -->
  <#elseif section == "info">
    <#if messagesPerField.existsError()>
      <div class="alert alert-danger" role="alert">
        <#list messagesPerField.allErrors as e>${e}</#list>
      </div>
    <#else>
      <div class="alert alert-success" role="alert">
        ${msg("emailSentText")}
      </div>
    </#if>

  <#-- SOCIAL PROVIDERS (si los usas) -->
  <#elseif section == "socialProviders">
    <#if social.providers?? && social.providers?has_content>
      <@identityProviders.show social=social/>
    </#if>
  </#if>

</@layout.registrationLayout>
