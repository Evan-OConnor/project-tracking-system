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

});