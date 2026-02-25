document.addEventListener("DOMContentLoaded", function () {

    const form = document.querySelector("form");
    const saveBtn = form.querySelector("button[type='submit']");

    form.addEventListener("submit", function (event) {

        if (!form.checkValidity()) {
            // Prevent submission if form is invalid
            event.preventDefault();
            event.stopPropagation();
        } else {
            // Show loading spinner
            saveBtn.disabled = true;
            saveBtn.innerHTML = `
                <span class="spinner-border spinner-border-sm me-2"></span>
                Saving...
            `;
        }

        form.classList.add("was-validated");
    });

    // Optional toast helper if you want
    function showToast(message, isError = false) {
        const toastEl = document.getElementById("appToast");
        const toastMessage = document.getElementById("toastMessage");
        if (!toastEl || !toastMessage) return;

        toastMessage.textContent = message;
        toastEl.classList.remove("bg-success", "bg-danger");
        toastEl.classList.add(isError ? "bg-danger" : "bg-success");
        new bootstrap.Toast(toastEl).show();
    }

});