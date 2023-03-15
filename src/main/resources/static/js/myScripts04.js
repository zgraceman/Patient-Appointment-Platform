//alert("connected test 04");

function selectClinicAndDoctor() {
    
}

function formOptionInput() {

    let obj = document.getElementById("selectForm00");

    if (obj.selectedIndex == 1) {
        document.getElementById("specialtyInput").value = "Cardiology";
    }
    else if (obj.selectedIndex == 2) {
        document.getElementById("specialtyInput").value = "Dermatology";
    }
    else if (obj.selectedIndex == 3) {
        document.getElementById("specialtyInput").value = "Endocrinology";
    }
    else if (obj.selectedIndex == 4) {
        document.getElementById("specialtyInput").value = "Family Medicine";
    }
    else if (obj.selectedIndex == 5) {
        document.getElementById("specialtyInput").value = "Gastroenterology";
    }
    else if (obj.selectedIndex == 6) {
        document.getElementById("specialtyInput").value = "Neurology";
    }
    else if (obj.selectedIndex == 7) {
        document.getElementById("specialtyInput").value = "Ophthalmology";
    }
    else if (obj.selectedIndex == 8) {
        document.getElementById("specialtyInput").value = "Pediatrics";
    }
    else if (obj.selectedIndex == 9) {
        document.getElementById("specialtyInput").value = "Podiatry";
    }
    else if (obj.selectedIndex == 10) {
        document.getElementById("specialtyInput").value = "Sleep Medicine";
    }
    
}

function symptomCheckboxInput() {

    if (document.getElementById("inlineCheckbox1").checked == true) {
        document.getElementById("symptom").innerHTML = "Based on your shortness of breath, we recommend <b>Cardiology</b>.";
        document.getElementById("br0").className = "";
    }
    if (document.getElementById("inlineCheckbox2").checked == true) {
        document.getElementById("symptom1").innerHTML = "Based on your chest pain/tightness, we recommend <b>Cardiology</b>.";
        document.getElementById("br1").className = "";
    }
    if (document.getElementById("inlineCheckbox3").checked == true) {
        document.getElementById("symptom2").innerHTML = "Based on your acne, we recommend <b>Dermatology</b>.";
        document.getElementById("br2").className = "";
    }
    if (document.getElementById("inlineCheckbox5").checked == true) {
        document.getElementById("symptom3").innerHTML = "Based on your dry/itchy skin, we recommend <b>Dermatology</b>.";
        document.getElementById("br3").className = "";
    }
    if (document.getElementById("inlineCheckbox6").checked == true) {
        document.getElementById("symptom4").innerHTML = "Based on your thyroid/hormone concerns, we recommend <b>Endocrinology</b>.";
        document.getElementById("br4").className = "";
    }
    if (document.getElementById("inlineCheckbox7").checked == true) {
        document.getElementById("symptom5").innerHTML = "Based on your mild fever, we recommend <b>Family Medicine</b>.";
        document.getElementById("br5").className = "";
    }
    if (document.getElementById("inlineCheckbox8").checked == true) {
        document.getElementById("symptom6").innerHTML = "Based on your digestive concerns, we recommend <b>Gastroenterology</b>.";
        document.getElementById("br6").className = "";
    }
    if (document.getElementById("inlineCheckbox9").checked == true) {
        document.getElementById("symptom7").innerHTML = "Based on your headaches, we recommend <b>Neurology</b>.";
        document.getElementById("br7").className = "";
    }
    if (document.getElementById("inlineCheckbox10").checked == true) {
        document.getElementById("symptom8").innerHTML = "Based on your stomach pain, we recommend <b>Gastroenterology</b>.";
        document.getElementById("br8").className = "";
    }
    if (document.getElementById("inlineCheckbox11").checked == true) {
        document.getElementById("symptom9").innerHTML = "Based on your dizziness, we recommend <b>Neurology</b>.";
        document.getElementById("br9").className = "";
    }
    if (document.getElementById("inlineCheckbox12").checked == true) {
        document.getElementById("symptom10").innerHTML = "Based on your blurry vision, we recommend <b>Ophthalmology</b>.";
        document.getElementById("br10").className = "";
    }
    if (document.getElementById("inlineCheckbox13").checked == true) {
        document.getElementById("symptom11").innerHTML = "Based on your child health concerns, we recommend <b>Pediatrics</b>.";
        document.getElementById("br11").className = "";
    }
    if (document.getElementById("inlineCheckbox14").checked == true) {
        document.getElementById("symptom12").innerHTML = "Based on your feet/ankle injury, we recommend <b>Podiatry</b>.";
        document.getElementById("br12").className = "";
    }
    if (document.getElementById("inlineCheckbox15").checked == true) {
        document.getElementById("symptom13").innerHTML = "Based on your insomnia, we recommend <b>Sleep Medicine</b>.";
        document.getElementById("br13").className = "";
    }

    document.getElementById("result").className = "vh-100 gradient-custom";
}

function refreshSymptomCheckboxInput() {

    location.reload();

}