document.addEventListener("DOMContentLoaded", function () {

    const deleteBtn = document.getElementById("deleteBtn");
    if (!deleteBtn) return;

    // ================================
    // CSRF Setup
    // ================================
    const csrfTokenMeta = document.querySelector("meta[name='_csrf']");
    const csrfHeaderMeta = document.querySelector("meta[name='_csrf_header']");

    const csrfToken = csrfTokenMeta ? csrfTokenMeta.content : null;
    const csrfHeader = csrfHeaderMeta ? csrfHeaderMeta.content : null;

    deleteBtn.addEventListener("click", function () {

        const id = this.dataset.id;

        if (!confirm("Delete this contact?")) return;

        if (!csrfToken || !csrfHeader) {
            alert("Security token missing. Please refresh the page.");
            return;
        }

        fetch(`/contacts/${id}/delete`, {
            method: "POST",
            headers: {
                [csrfHeader]: csrfToken
            }
        })
        .then(response => {
            if (response.ok) {
                window.location.href = "/contacts";
            } else {
                alert("Delete failed.");
            }
        })
        .catch(() => {
            alert("Server error occurred.");
        });

    });

});