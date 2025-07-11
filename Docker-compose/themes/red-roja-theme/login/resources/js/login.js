// resources/js/login.js
document.addEventListener('DOMContentLoaded', () => {
  const form      = document.getElementById('kc-form-login');
  const inputUser = document.getElementById('inputUsername');
  const btnLogin  = document.getElementById('kc-login');

  if (!form || !inputUser || !btnLogin) return;

  const isValid = val => /^\d{1,4}$/.test(val);

  // Al enviar, validamos y paddeamos
  form.addEventListener('submit', e => {
    const raw = inputUser.value.trim();
    if (!isValid(raw)) {
      e.preventDefault();
      inputUser.classList.add('is-invalid');
      // Si no existe ya un feedback, lo creamos
      if (!inputUser.parentNode.querySelector('.invalid-feedback')) {
        const fb = document.createElement('div');
        fb.className = 'invalid-feedback';
        fb.textContent = 'El usuario debe tener entre 1 y 4 dígitos.';
        inputUser.parentNode.appendChild(fb);
      }
      btnLogin.disabled = false; // por si estuviera deshabilitado
      return;
    }

    // Válido: rellenamos y dejamos que se envíe
    inputUser.value = raw.padStart(4, '0');
  });

  // Mientras el usuario escribe, quitamos el error tan pronto es válido
  inputUser.addEventListener('input', () => {
    const raw = inputUser.value.trim();
    if (isValid(raw)) {
      inputUser.classList.remove('is-invalid');
      const fb = inputUser.parentNode.querySelector('.invalid-feedback');
      if (fb) fb.remove();
    }
  });
});
