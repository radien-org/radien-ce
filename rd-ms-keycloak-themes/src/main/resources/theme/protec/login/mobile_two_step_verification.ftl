<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>
    <link href="${url.resourcesPath}/css/intlTelInput.css" rel="stylesheet" />
    <script src="${url.resourcesPath}/js/intlTelInput.js" type="text/javascript"></script>
    <script src="${url.resourcesPath}/js/utils.js" type="text/javascript"></script>
    <script src="${url.resourcesPath}/js/data.js" type="text/javascript"></script>
    <#if section = "header">
        ${msg("smsAuthTitle",realm.displayName)}
    <#elseif section = "form">
        <form class="form-actions" action="${url.loginAction}" method="POST">
            <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcInputWrapperClass!}">
                    <input type="tel"
                           id="field_mobile_number"
                           aria-invalid="<#if messagesPerField.existsError('user.attributes.mobile_number')>true</#if>"
                           placeholder="${msg("mobile_number")}"
                           name="field_mobile_number"
                           value="${field_mobile_number!''}"/>
                    <input type="hidden"
                           id="mobile_number"
                           name="user.attributes.mobile_number"
                           value="${field_mobile_number!''}"/>
                    <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}"
                           name="sendSMS"
                           id="kc-sendSMS"
                           type="submit"
                           value="sendSMS"/>

                    <script>
                        var input = document.querySelector("#field_mobile_number");
                        var iti =window.intlTelInput(input, {
                            initialCountry: "de",
                            onlyCountries: ['BE','BG', 'CZ', 'DK', 'DE', 'EE', 'IE', 'EL', 'ES', 'FR', 'GB', 'HR', 'IT', 'CY', 'LV', 'LT',
                                'LU', 'HU', 'MT', 'NL', 'AT', 'PL', 'PT', 'RO', 'SI', 'SK', 'FI', 'SE']
                        });
                        var handleChange = function() {
                            let dialCode = iti.getSelectedCountryData().dialCode;
                            let number = iti._getFullNumber();
                            number = '+' + dialCode + number;
                            document.getElementById("mobile_number").setAttribute('value', number);
                        }
                        input.addEventListener('countrychange', handleChange);
                        input.addEventListener('change', handleChange);
                        input.addEventListener('keyup', handleChange);
                    </script>

                    <#if messagesPerField.existsError('user.attributes.mobile_number')>
                        <span id="input-error-mobile_number" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                ${kcSanitize(messagesPerField.get('user.attributes.mobile_number'))?no_esc}
                            </span>
                    </#if>
                </div>
            </div>

            <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="code" class="${properties.kcLabelClass!}">${msg("smsAuthLabel")}</label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input type="text" id="code" name="code" class="${properties.kcInputClass!}" autofocus/>
                </div>
            </div>


            <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}" name="submit" id="kc-submit" type="submit" value="submit"/>
        </form>
        <div class="clearfix"></div>
    </#if>
</@layout.registrationLayout>