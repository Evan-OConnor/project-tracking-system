document.addEventListener("DOMContentLoaded", function () {

    // ================================
    // Live Search
    // ================================
    const searchInput = document.getElementById("searchInput");
    const tableRows = document.querySelectorAll("#contactsTable tr");

    if (searchInput) {
        searchInput.addEventListener("keyup", function () {
            const filter = this.value.toLowerCase();

            tableRows.forEach(row => {
                const nameCell = row.cells[1];
                if (!nameCell) return;

                const name = nameCell.textContent.toLowerCase();
                row.style.display = name.includes(filter) ? "" : "none";
            });
        });
    }

    // ================================
    // CSRF Setup
    // ================================
    const csrfTokenMeta = document.querySelector("meta[name='_csrf']");
    const csrfHeaderMeta = document.querySelector("meta[name='_csrf_header']");

    const csrfToken = csrfTokenMeta ? csrfTokenMeta.content : null;
    const csrfHeader = csrfHeaderMeta ? csrfHeaderMeta.content : null;

    // ================================
    // AJAX Delete
    // ================================
    document.querySelectorAll(".delete-btn").forEach(button => {

        button.addEventListener("click", function () {

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
                    this.closest("tr").remove();
                } else {
                    alert("Delete failed.");
                }
            })
            .catch(() => {
                alert("Server error occurred.");
            });

        });

    });

});