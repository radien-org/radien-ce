import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { WizardComponent } from './wizard/wizard.component';
import { CookieGuideLineComponent } from './cookie-guide-line/cookie-guide-line.component';
import { ImpressumComponent } from './impressum/impressum.component';
import { BeginComponent } from './begin/begin.component';
import { AccidentComponent } from './accident/accident.component';
import { AccidentTypeComponent } from './accident-type/accident-type.component';
import { LegalDataPolicyComponent } from './legal-data-policy/legal-data-policy.component';
import { DieDetailsComponent } from './die-details/die-details.component';
import { AccidentDateComponent } from './accident-date/accident-date.component';

const routes: Routes = [
  {
    path: '',
    component: WizardComponent
  },
  {
    path: 'wizard',
    component: WizardComponent
  },
  {
    path: 'begin',
    component: BeginComponent
  },
  {
    path: 'cookie-guide-line',
    component: CookieGuideLineComponent
  },
  {
    path: 'impressum',
    component: ImpressumComponent
  },
  {
    path: 'accident-intro',
    component: AccidentComponent
  },
  {
    path: 'accident-type',
    component: AccidentTypeComponent
  },
  {
    path: 'accident-date',
    component: AccidentDateComponent
  },
  {
    path: 'legal-data-policy',
    component: LegalDataPolicyComponent
  },
  {
    path: 'die-details',
    component: DieDetailsComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DataAcquisitionRoutingModule {}
