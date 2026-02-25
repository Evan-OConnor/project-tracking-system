document.addEventListener("DOMContentLoaded", function () {
    // Search filter
    const searchInput = document.getElementById("searchInput");
    const tableRows = Array.from(document.querySelectorAll("#invoicesTable tbody tr"))
        .filter(r => r.querySelectorAll('td').length > 0);

    if (searchInput) {
        searchInput.addEventListener("keyup", function () {
            const filter = this.value.toLowerCase();
            tableRows.forEach(row => {
                const projectCell = row.cells[1];
                row.style.display = projectCell.textContent.toLowerCase().includes(filter) ? "" : "none";
            });
        });
    }

    // Delete buttons
    document.querySelectorAll(".delete-btn").forEach(button => {
        button.addEventListener("click", function () {
            const id = this.dataset.id;
            if (!confirm("Delete this invoice?")) return;

            fetch(`/invoices/${id}/delete`, {
                method: "POST",
                headers: { 'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content }
            })
                .then(res => {
                    if (res.ok) {
                        this.closest("tr").remove();
                        showToast("Invoice deleted successfully");
                    } else {
                        showToast("Delete failed", true);
                    }
                })
                .catch(() => showToast("Server error occurred", true));
        });
    });

    function showToast(message, isError = false) {
        const toastEl = document.createElement("div");
        toastEl.className = `toast align-items-center text-white border-0 ${isError ? 'bg-danger' : 'bg-success'}`;
        toastEl.style.position = "fixed";
        toastEl.style.bottom = "20px";
        toastEl.style.right = "20px";
        toastEl.style.zIndex = "9999";
        toastEl.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">${message}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" onclick="this.closest('.toast').remove()"></button>
            </div>
        `;
        document.body.appendChild(toastEl);
        setTimeout(() => toastEl.remove(), 4000);
    }
});