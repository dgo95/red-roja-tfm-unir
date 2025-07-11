<#-- 1) Calcula la URL según el locale -->
<#assign langTag = locale.currentLanguageTag />

<#-- 2) Comparamos contra "ca" (o "ca-ES" si lo usas) -->
<#if langTag == "ca" || langTag == "ca-ES">
  <#assign bgUrl = url.resourcesPath + "/img/1500x500-ca_ES.jpg" />
<#else>
  <#assign bgUrl = url.resourcesPath + "/img/1500x500-es_ES.jpg" />
</#if>

<#-- 2) Inyecta dinámicamente la variable CSS -->
<style>
  :root {
    --kc-bg-url: url("${bgUrl}");
  }
</style>
<#import "template.ftl" as layout>
<#import "social-providers.ftl" as identityProviders>

<@layout.registrationLayout
    displayMessage = !messagesPerField.existsError('username','password')
    displayInfo    = realm.password && realm.registrationAllowed && !registrationDisabled??;
    section>

  <#-- HEADER: solo el título principal -->
  <#if section == "header">
    ${msg("loginAccountTitle")}

  <#-- FORM: imagen a la izquierda, formulario a la derecha -->
  <#elseif section == "form">
    <div class="container-fluid ps-md-0 h-100">
      <div class="row g-0 h-100">

        <!-- DERECHA: formulario centrado -->
        <div class="col-12 d-flex align-items-center justify-content-center h-100">
          <div class="w-100 px-3 px-md-5" style="max-width:480px;">
            <form
              id="kc-form-login"
              class="${properties.kcFormClass!}"
              onsubmit="login.disabled = true; return true;"
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
				  inputmode="numeric"
				  pattern="\d{1,4}"
				  maxlength="4"
                  class="form-control <#if messagesPerField.existsError('username')>is-invalid</#if>"
                  placeholder="${msg("username")}"
                  value="${login.username!''}" />
                <label for="inputUsername">${msg("username")}</label>
                <#if messagesPerField.existsError('username')>
				  <div class="invalid-feedback">
					<#assign errs = messagesPerField.get('username')>
					<#if errs?is_string>
					  ${errs}
					<#else>
					  <#list errs as m>${m}</#list>
					</#if>
				  </div>
				</#if>
              </div>

              <!-- Password -->
              <div class="form-floating mb-3">
                <input
                  type="password"
                  name="password"
                  id="inputPassword"
                  autocomplete="current-password"
                  class="form-control <#if messagesPerField.existsError('password')>is-invalid</#if>"
                  placeholder="${msg("password")}" />
                <label for="inputPassword">${msg("password")}</label>
                <#if messagesPerField.existsError('password')>
				  <div class="invalid-feedback">
					<#assign errs = messagesPerField.get('password')>
					<#if errs?is_string>
					  ${errs}
					<#else>
					  <#list errs as m>${m}</#list>
					</#if>
				  </div>
				</#if>
              </div>

              <!-- Forgot Password -->
              <#if realm.resetPasswordAllowed>
                <div class="mb-3">
                  <a href="${url.loginResetCredentialsUrl}" class="small">
                    ${msg("doForgotPassword")}
                  </a>
                </div>
              </#if>

              <!-- Remember Me -->
              <#if realm.rememberMe>
                <div class="form-check mb-3">
                  <input
                    class="form-check-input"
                    type="checkbox"
                    id="rememberMe"
                    name="rememberMe"
                    <#if login.rememberMe>checked</#if> />
                  <label class="form-check-label" for="rememberMe">
                    ${msg("rememberMe")}
                  </label>
                </div>
              </#if>

              <!-- Botón de login -->
              <div class="d-grid">
                <button
                  type="submit"
                  id="kc-login"
                  name="login"
                  class="btn btn-lg btn-danger btn-login text-uppercase fw-bold mb-2">
                  ${msg("doLogIn")}
                </button>
              </div>

            </form>
          </div>
        </div>

      </div>
    </div>

  <#-- INFO (registro) -->
  <#elseif section == "info">
    <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
      <div id="kc-registration-container" class="${properties.kcLoginFooterBand!}">
        <div id="kc-registration" class="${properties.kcLoginFooterBandItem!}">
          <span>
            ${msg("noAccount")}
            <a href="${url.registrationUrl}">${msg("doRegister")}</a>
          </span>
        </div>
      </div>
    </#if>

  <#-- SOCIAL PROVIDERS -->
  <#elseif section == "socialProviders">
    <#if realm.password && social.providers?? && social.providers?has_content>
      <@identityProviders.show social=social/>
    </#if>
  </#if>
  <script src="${url.resourcesPath}/js/login.js"></script>
</@layout.registrationLayout>