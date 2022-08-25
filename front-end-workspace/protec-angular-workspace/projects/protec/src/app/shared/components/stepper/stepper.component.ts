import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {TranslateService} from "@ngx-translate/core";

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
      label: this.translationService.instant('step1'),
      type: 'arrow-right-first',
      link: '/data-acquisition/accident-intro',
      status: 'untouched',
      step: 1
    },
    {
      label: this.translationService.instant('step2'),
      type: 'arrow-right',
      link: '/data-acquisition/die-details',
      status: 'untouched',
      step: 2
    },
    {
      label: this.translationService.instant('step3'),
      type: 'arrow-right',
      link: '/',
      status: 'untouched',
      step: 3
    },
    {
      label: this.translationService.instant('step4'),
      type: 'arrow-right',
      link: '/',
      status: 'untouched',
      step: 4
    }
  ]

  lastStep = {
    label: this.translationService.instant('step_final'),
    type: 'default',
    link: '/'
  }

  constructor(private readonly translationService: TranslateService, private _formBuilder: FormBuilder) {}

  ngOnInit() {
    this.firstFormGroup = this._formBuilder.group({
      firstCtrl: ['', Validators.required],
    });
    this.secondFormGroup = this._formBuilder.group({
      secondCtrl: ['', Validators.required],
    });
  }

}
