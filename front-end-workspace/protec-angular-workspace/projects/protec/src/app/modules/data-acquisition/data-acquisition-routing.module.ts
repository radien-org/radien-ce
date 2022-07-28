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
import { TermsOfUseComponent } from './terms-of-use/terms-of-use.component';
import { AGBComponent } from "./AGB/AGB.component";
import { MoreInjuriesComponent } from "./more-injuries/more-injuries.component";
import { WeAreSorryComponent } from "./we-are-sorry/we-are-sorry.component";
import { YourDataComponent } from "./your-data/your-data.component";
import { ProspectsOfSuccess } from "./prospects-of-success/prospects-of-success.component"
import { RequestSendComponent } from "./request-send/request-send.component";
import {IllnessComponent} from "./illness/illness.component";
import {Illness2Component} from "./illness-2/illness-2.component";

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
  },
  {
    path: 'terms-of-use',
    component: TermsOfUseComponent
  },
  {
    path: 'agb',
    component: AGBComponent
  },
  {
    path: 'more-injuries',
    component: MoreInjuriesComponent
  },
  {
    path: 'we-are-sorry',
    component: WeAreSorryComponent
  },
  {
    path: 'your-data',
    component: YourDataComponent
  },
  {
    path: 'prospects-of-success',
    component: ProspectsOfSuccess
  },
  {
    path: 'request-send',
    component: RequestSendComponent
  },
  {
    path: 'illness',
    component: IllnessComponent
  },
  {
    path: 'illness-2',
    component: Illness2Component
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DataAcquisitionRoutingModule {}
