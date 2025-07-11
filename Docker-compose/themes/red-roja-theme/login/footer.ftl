<#macro content>
  <div id="kc-footer" class="${properties.kcLoginFooterClass!}">

    <#-- 1) wrapper full‑width, flex-end  -->
    <div class="w-100 d-flex justify-content-end mb-3">
      <select
        aria-label="${msg("languages")}"
        id="login-select-toggle"
        class="form-select form-select-sm w-auto flex-shrink-0 me-4"
        onchange="if (this.value) window.location.href=this.value"
      >
        <#list locale.supported?sort_by("label") as l>
          <option
            value="${l.url}"
            ${(l.languageTag == locale.currentLanguageTag)?then('selected','')}
          >
            ${l.label}
          </option>
        </#list>
      </select>
    </div>

    <#-- 2) tus socialProviders y registro (igual que antes) -->
    <#nested "socialProviders">
    <#if realm.password?default(false) && realm.registrationAllowed?default(false) && ! (registrationDisabled?default(false))>
      <div id="kc-registration-container" class="${properties.kcLoginFooterBand!}">
        <div id="kc-registration" class="${properties.kcLoginFooterBandItem!}">
          <span>
            ${msg("noAccount")}
            <a href="${url.registrationUrl}">${msg("doRegister")}</a>
          </span>
        </div>
      </div>
    </#if>

  </div>
</#macro>
