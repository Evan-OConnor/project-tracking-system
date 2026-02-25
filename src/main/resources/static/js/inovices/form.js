// form.js

document.addEventListener('DOMContentLoaded', () => {
    const tableBody = document.querySelector('#line-items-table tbody');

    // Update totals whenever quantity or unitRate changes
    tableBody.addEventListener('input', (e) => {
        if (e.target.matches('input')) {
            updateTotals();
        }
    });
});

// Add a new row
function addRow() {
    const tableBody = document.querySelector('#line-items-table tbody');
    const rowCount = tableBody.rows.length;

    const row = document.createElement('tr');
    row.innerHTML = `
        <td><input type="text" name="items[${rowCount}].description" class="form-control" required></td>
        <td><input type="text" name="items[${rowCount}].details" class="form-control"></td>
        <td><input type="number" step="0.01" name="items[${rowCount}].quantity" class="form-control" required></td>
        <td><input type="number" step="0.01" name="items[${rowCount}].unitRate" class="form-control" required></td>
        <td><button type="button" class="btn btn-sm btn-danger" onclick="removeRow(this)">Remove</button></td>
    `;
    tableBody.appendChild(row);
}

// Remove a row
function removeRow(button) {
    const row = button.closest('tr');
    row.remove();
    updateTotals();
}

// Update subtotal, VAT, and total including VAT
function updateTotals() {
    const tableBody = document.querySelector('#line-items-table tbody');
    let subtotal = 0;

    tableBody.querySelectorAll('tr').forEach(row => {
        const qtyInput = row.querySelector('input[name*=".quantity"]');
        const rateInput = row.querySelector('input[name*=".unitRate"]');

        const qty = parseFloat(qtyInput.value) || 0;
        const rate = parseFloat(rateInput.value) || 0;

        const total = qty * rate;
        subtotal += total;
    });

    // Update hidden inputs in the form
    const vatSelect = document.querySelector('select[name="vatRateId"]');
    let vatPercent = 0;
    if (vatSelect) {
        const selectedOption = vatSelect.options[vatSelect.selectedIndex];
        vatPercent = parseFloat(selectedOption.text.replace('%', '')) || 0;
    }

    const vatTotal = (subtotal * vatPercent) / 100;
    const totalIncludingVat = subtotal + vatTotal;

    // If you have hidden inputs for totals in your DTO, set them
    setOrCreateHiddenInput('subtotal', subtotal.toFixed(2));
    setOrCreateHiddenInput('vatTotal', vatTotal.toFixed(2));
    setOrCreateHiddenInput('totalIncludingVat', totalIncludingVat.toFixed(2));
}

// Helper to create/update hidden input fields for totals
function setOrCreateHiddenInput(name, value) {
    let input = document.querySelector(`input[name="${name}"]`);
    if (!input) {
        input = document.createElement('input');
        input.type = 'hidden';
        input.name = name;
        document.querySelector('form').appendChild(input);
    }
    input.value = value;
}