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
      type: 'arrow-right-first',
      link: '/data-acquisition/accident-intro'
    },
    {
      label: 'DIE DETAILS',
      type: 'arrow-right',
      link: '/data-acquisition/die-details'
    },
    {
      label: 'ERFOLGSAUSSICHTEN',
      type: 'arrow-right',
      link: '/'
    },
    {
      label: 'DEINE DATEN',
      type: 'arrow-right',
      link: '/'
    }
  ]

  lastStep = {
    label: 'ANGEBOT ANFORDERN',
    type: 'default',
    link: '/'
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
