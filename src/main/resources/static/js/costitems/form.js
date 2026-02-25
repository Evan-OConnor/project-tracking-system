document.addEventListener("DOMContentLoaded", function () {

    const typeSelect = document.getElementById('typeSelect');
    const supplierSelect = document.getElementById('supplierSelect');

    function toggleSupplier() {
        if (!supplierSelect || !typeSelect) return;
        supplierSelect.disabled = typeSelect.value === 'EXPENSE';
    }

    typeSelect.addEventListener('change', toggleSupplier);
    toggleSupplier();

    // Optional: add form validation with loading button
    const form = document.querySelector("form");
    const saveBtn = form.querySelector("button[type='submit']");

    form.addEventListener("submit", function (event) {
        if (!form.checkValidity()) {
            event.preventDefault();
            event.stopPropagation();
        } else {
            saveBtn.disabled = true;
            saveBtn.innerHTML = `<span class="spinner-border spinner-border-sm me-2"></span> Saving...`;
        }
        form.classList.add("was-validated");
    });

});