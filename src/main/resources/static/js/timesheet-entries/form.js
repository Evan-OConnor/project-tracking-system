document.addEventListener("DOMContentLoaded", function () {

    const form = document.querySelector("form");
    const saveBtn = form ? form.querySelector("button[type='submit']") : null;

    const workDescriptionSelect = document.getElementById("workDescriptionSelect");
    const otherDescriptionContainer = document.getElementById("otherDescriptionContainer");

    // Toggle "Other Description" field
    function toggleOtherField() {

        if (!workDescriptionSelect || !otherDescriptionContainer) return;

        const selectedText =
            workDescriptionSelect.options[workDescriptionSelect.selectedIndex]?.text;

        if (selectedText && selectedText.toLowerCase() === "other") {
            otherDescriptionContainer.style.display = "block";
        } else {
            otherDescriptionContainer.style.display = "none";
        }
    }

    if (workDescriptionSelect) {
        workDescriptionSelect.addEventListener("change", toggleOtherField);
        toggleOtherField();
    }

    // Form validation + loading spinner
    if (form) {
        form.addEventListener("submit", function (event) {

            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            } else if (saveBtn) {

                saveBtn.disabled = true;
                saveBtn.innerHTML =
                    `<span class="spinner-border spinner-border-sm me-2"></span>Saving...`;

            }

            form.classList.add("was-validated");
        });
    }

});