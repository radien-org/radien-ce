import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-stepper',
  templateUrl: './stepper.component.html',
  styleUrls: ['./stepper.component.scss']
})
export class StepperComponent implements OnInit {

  firstFormGroup!: FormGroup;
  secondFormGroup!: FormGroup;
  steps = [
    {
      label: 'DAS SCHADENSEREIGNIS',
      type: 'arrow-right'
    },
    {
      label: 'DIE DETAILS',
      type: 'arrow-right'
    },
    {
      label: 'ERFOLGSAUSSICHTEN',
      type: 'arrow-right'
    },
    {
      label: 'DEINE DATEN',
      type: 'arrow-right'
    }
  ]

  lastStep = {
    label: 'ANGEBOT ANFORDERN',
    type: 'default'
  }

  constructor(private _formBuilder: FormBuilder) {}

  ngOnInit() {
    this.firstFormGroup = this._formBuilder.group({
      firstCtrl: ['', Validators.required],
    });
    this.secondFormGroup = this._formBuilder.group({
      secondCtrl: ['', Validators.required],
    });
  }

}
