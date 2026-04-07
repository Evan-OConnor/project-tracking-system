document.addEventListener("DOMContentLoaded", function () {

    console.log("JS running");

    const typeSelect = document.getElementById("typeSelect");
    const supplierField = document.getElementById("supplierField");
    const supplierSelect = document.getElementById("supplierSelect");

    if (!typeSelect || !supplierField) {
        console.log("Elements not found");
        return;
    }

  function toggleSupplier() {

      const value = typeSelect.value;

      if (!value) {
          supplierField.style.display = "none"; // hide until chosen
          return;
      }

      if (value.toUpperCase() === "EXPENSE") {
          supplierField.style.display = "none";
          supplierSelect.value = "";
      } else {
          supplierField.style.display = "block";
      }
  }
      toggleSupplier();
      typeSelect.addEventListener("change", toggleSupplier); // run on change
  });