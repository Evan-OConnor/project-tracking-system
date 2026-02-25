document.addEventListener("DOMContentLoaded", function () {

    const form = document.querySelector("form");
    const saveBtn = form.querySelector("button[type='submit']");

    form.addEventListener("submit", function (event) {

        if (!form.checkValidity()) {
            event.preventDefault();
            event.stopPropagation();
        } else {
            // Loading state
            saveBtn.disabled = true;
            saveBtn.innerHTML = `
                <span class="spinner-border spinner-border-sm me-2"></span>
                Saving...
            `;
        }

        form.classList.add("was-validated");
    });

});