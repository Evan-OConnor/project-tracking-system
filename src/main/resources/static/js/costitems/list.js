document.addEventListener("DOMContentLoaded", function () {

    const searchInput = document.createElement("input");
    searchInput.type = "text";
    searchInput.placeholder = "Search...";
    searchInput.className = "form-control mb-3";
    const container = document.querySelector(".container");
    container.insertBefore(searchInput, container.querySelector(".table-responsive"));

    const tableRows = document.querySelectorAll("table tbody tr");

    // Live Search
    searchInput.addEventListener("keyup", function () {
        const filter = this.value.toLowerCase();
        tableRows.forEach(row => {
            let match = false;
            row.querySelectorAll("td").forEach(td => {
                if (td.textContent.toLowerCase().includes(filter)) match = true;
            });
            row.style.display = match ? "" : "none";
        });
    });

    // AJAX Delete
    document.querySelectorAll("button.delete-btn").forEach(button => {
        button.addEventListener("click", function () {
            const id = this.dataset.id;
            if (!confirm("Are you sure you want to delete this item?")) return;

            fetch(`/cost-items/${id}/delete`, { method: "POST" })
                .then(response => {
                    if (response.ok) {
                        this.closest("tr").remove();
                        showToast("Cost item deleted successfully");
                    } else {
                        showToast("Delete failed", true);
                    }
                })
                .catch(() => showToast("Server error occurred", true));
        });
    });

    function showToast(message, isError = false) {
        const toastEl = document.getElementById("appToast");
        const toastMessage = document.getElementById("toastMessage");
        if (!toastEl || !toastMessage) return;

        toastMessage.textContent = message;
        toastEl.classList.remove("bg-success","bg-danger");
        toastEl.classList.add(isError ? "bg-danger" : "bg-success");
        new bootstrap.Toast(toastEl).show();
    }

});