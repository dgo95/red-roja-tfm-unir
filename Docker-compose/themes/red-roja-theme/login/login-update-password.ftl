<#-- login-update-password.ftl -->

<#-- 1) Lang -->
<#assign langTag = locale.currentLanguageTag />

<#-- 2) Asigna bgUrl según locale -->
<#if langTag?starts_with("ca")>
  <#assign bgUrl = url.resourcesPath + "/img/1500x500-ca_ES.jpg" />
<#else>
  <#assign bgUrl = url.resourcesPath + "/img/1500x500-es_ES.jpg" />
</#if>

<style>
  :root { --kc-bg-url: url("${bgUrl}") }
</style>

<#import "template.ftl"      as layout>
<#import "password-commons.ftl" as passwordCommons>

<@layout.registrationLayout
    displayMessage = !messagesPerField.existsError('password','password-confirm')
; section>

  <#if section == "header">
    ${msg("updatePasswordTitle")}

  <#elseif section == "form">
    <div class="container-fluid ps-md-0 h-100">
      <div class="row g-0 h-100">
        <div class="col-12 d-flex align-items-center justify-content-center h-100">
          <div class="w-100 px-3 px-md-5" style="max-width:480px;">

            <!-- 📋 Aquí arranca el FORM del tema base, sin tocarlo -->
            <form id="kc-passwd-update-form"
                  class="${properties.kcFormClass!}"
                  action="${url.loginAction}"
                  method="post">

              <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">
                  <label for="password-new" class="${properties.kcLabelClass!}">
                    ${msg("passwordNew")}
                  </label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                  <div class="${properties.kcInputGroup!}" dir="ltr">
                    <input type="password"
                           id="password-new"
                           name="password-new"
                           class="${properties.kcInputClass!}"
                           autofocus
                           autocomplete="new-password"
                           aria-invalid="<#if messagesPerField.existsError('password')>true</#if>" />
                    <button class="${properties.kcFormPasswordVisibilityButtonClass!}"
                            type="button"
                            aria-label="${msg('showPassword')}"
                            aria-controls="password-new"
                            data-password-toggle
                            data-icon-show="${properties.kcFormPasswordVisibilityIconShow!}"
                            data-icon-hide="${properties.kcFormPasswordVisibilityIconHide!}"
                            data-label-show="${msg('showPassword')}"
                            data-label-hide="${msg('hidePassword')}">
                      <i class="${properties.kcFormPasswordVisibilityIconShow!}" aria-hidden="true"></i>
                    </button>
                  </div>
                  <#if messagesPerField.existsError('password')>
                    <span id="input-error-password"
                          class="${properties.kcInputErrorMessageClass!}"
                          aria-live="polite">
                      ${kcSanitize(messagesPerField.get('password'))?no_esc}
                    </span>
                  </#if>
                </div>
              </div>

              <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">
                  <label for="password-confirm" class="${properties.kcLabelClass!}">
                    ${msg("passwordConfirm")}
                  </label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                  <div class="${properties.kcInputGroup!}" dir="ltr">
                    <input type="password"
                           id="password-confirm"
                           name="password-confirm"
                           class="${properties.kcInputClass!}"
                           autocomplete="new-password"
                           aria-invalid="<#if messagesPerField.existsError('password-confirm')>true</#if>" />
                    <button class="${properties.kcFormPasswordVisibilityButtonClass!}"
                            type="button"
                            aria-label="${msg('showPassword')}"
                            aria-controls="password-confirm"
                            data-password-toggle
                            data-icon-show="${properties.kcFormPasswordVisibilityIconShow!}"
                            data-icon-hide="${properties.kcFormPasswordVisibilityIconHide!}"
                            data-label-show="${msg('showPassword')}"
                            data-label-hide="${msg('hidePassword')}">
                      <i class="${properties.kcFormPasswordVisibilityIconShow!}" aria-hidden="true"></i>
                    </button>
                  </div>
                  <#if messagesPerField.existsError('password-confirm')>
                    <span id="input-error-password-confirm"
                          class="${properties.kcInputErrorMessageClass!}"
                          aria-live="polite">
                      ${kcSanitize(messagesPerField.get('password-confirm'))?no_esc}
                    </span>
                  </#if>
                </div>
              </div>

              <div class="${properties.kcFormGroupClass!}">
                <!-- Botón “Cerrar otras sesiones” si existen -->
                <@passwordCommons.logoutOtherSessions/>
                <div id="kc-form-buttons"
                     class="${properties.kcFormButtonsClass!}">
                  <#if isAppInitiatedAction??>
                    <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}"
                           type="submit"
                           value="${msg("doSubmit")}" />
                    <button class="${properties.kcButtonClass!} ${properties.kcButtonDefaultClass!} ${properties.kcButtonLargeClass!}"
                            type="submit"
                            name="cancel-aia"
                            value="true">
                      ${msg("doCancel")}
                    </button>
                  <#else>
                    <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                           type="submit"
                           value="${msg("doSubmit")}" />
                  </#if>
                </div>
              </div>
            </form>

            <!-- Este script hace visible/oculto el ojo de la contraseña -->
            <script type="module"
                    src="${url.resourcesPath}/js/passwordVisibility.js">
            </script>
            <!-- 📋 Fin del FORM del tema base -->

          </div>
        </div>
      </div>
    </div>
  </#if>
</@layout.registrationLayout>