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
import { RequestQuoteComponent } from "./request-quote/request-quote.component";
import { IllnessComponent } from "./illness/illness.component";
import { Illness2Component } from "./illness-2/illness-2.component";
import { PrivateAccidentComponent } from "./private-accident/private-accident.component";
import { DetailsFigureComponent } from './details-figure/details-figure.component';
import { AdditionalInsuranceComponent } from './additional-insurance/additional-insurance.component';
import { DetailsIntroComponent } from './details-intro/details-intro.component';
import { PersonalDataInputComponent } from "./personal-data-input/personal-data-input.component";
import { PersonalDataInputComponentTwo } from "./personal-data-input-2/personal-data-input-2.component";
import { WorkAccidentComponent } from './work-accident/work-accident.component';
import { PartBodyFigureComponent } from './part-body-figure/part-body-figure.component';
import { SummaryComponent } from './summary/summary.component';
import { ChanceOfSuccessComponent } from './chance-of-success/chance-of-success.component';
import { PersonalDataInformationComponent } from './personal-data-information/personal-data-information.component';
import { PersonalDataSummaryComponent } from './personal-data-summary/personal-data-summary.component';
import { AppreciationComponent } from './appreciation/appreciation.component';
import { PreSummaryComponent } from './pre-summary/pre-summary.component';

const routes: Routes = [
  {
    path: '',
    component: WizardComponent
  },
  //cookies and footer links START
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
    path: 'terms-of-use',
    component: TermsOfUseComponent
  },
  {
    path: 'agb',
    component: AGBComponent
  },
  {
    path: 'legal-data-policy',
    component: LegalDataPolicyComponent
  },
  //cookies and footer links END
  //generic routes START
  {
    path: 'we-are-sorry',
    component: WeAreSorryComponent
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
    path: 'request-send',
    component: RequestSendComponent
  },
  //generic routes END
  {
    path: 'wizard',
    component: WizardComponent
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
    path: 'additional-insurance',
    component: AdditionalInsuranceComponent
  },
  {
    path: 'accident-date',
    component: AccidentDateComponent
  },
  {
    path: 'details-intro',
    component: DetailsIntroComponent
  },
  {
    path: 'work-accident',//variation one
    component: WorkAccidentComponent
  },
  {
    path: 'illness-details',//variation two
    component: IllnessComponent
  },
  {
    path: 'illness-diagnostic',
    component: Illness2Component
  },
  {
    path: 'private-accident',//variation three
    component: PrivateAccidentComponent
  },
  {
    path: 'full-body',
    component: DetailsFigureComponent
  },
  {
    path: 'part-body',
    component: PartBodyFigureComponent
  },
  {
    path: 'more-injuries',
    component: MoreInjuriesComponent
  },
  {
    path: 'summary',
    component: SummaryComponent
  },
  {
    path: 'chance-of-success',
    component: ChanceOfSuccessComponent
  },
  {
    path: 'request-quote',
    component: RequestQuoteComponent
  },
  {
    path: 'personal-data-information',
    component: PersonalDataInformationComponent
  },
  {
    path: 'your-data',//TODO: check if we really need this page
    component: YourDataComponent
  },
  {
    path: 'personal-data-person',
    component: PersonalDataInputComponent
  },
  {
    path: 'personal-data-contact',
    component: PersonalDataInputComponentTwo
  },
  {
    path: 'personal-data-summary',//TODO: check if we really need this page, because we remove in task https://radien.atlassian.net/browse/PROTE-527
    component: PersonalDataSummaryComponent
  },
  {
    path: 'appreciation',
    //component: AppreciationComponent,
    component: RequestSendComponent,
  },
  {
    path: 'pre-summary',
    component: PreSummaryComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DataAcquisitionRoutingModule {}
