document.addEventListener("DOMContentLoaded", function () {

    // Live Search
    const searchInput = document.getElementById("searchInput");
    const tableRows = document.querySelectorAll("#contactsTable tr");

    if (searchInput) {
        searchInput.addEventListener("keyup", function () {

            const filter = this.value.toLowerCase();

            tableRows.forEach(row => {

                const idCell = row.cells[0];
                const nameCell = row.cells[1];
                const phoneCell = row.cells[2];
                if (!idCell || !nameCell || !phoneCell) return;

                const idText = idCell.textContent.trim();
                const nameText = nameCell.textContent.toLowerCase();
                const phoneText = phoneCell.textContent.trim();

                 const match =
                                    idText.includes(filter) ||
                                    nameText.includes(filter) ||
                                    phoneText.includes(filter);


                row.style.display = match ? "" : "none";

            });

        });
    }

});