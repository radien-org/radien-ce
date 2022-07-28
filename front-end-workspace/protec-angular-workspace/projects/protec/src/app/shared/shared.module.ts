import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../material.module';
import { CalendarModule } from 'primeng/calendar';
import { TranslateModule } from '@ngx-translate/core';
//import { FieldFormatterPipe } from './pipes/field-formatter.pipe';
import { StepperComponent } from './components/stepper/stepper.component';
import { GButtonComponent } from './components/g-button/g-button.component';
import { SidenavBackdropComponent } from './components/sidenav-backdrop/sidenav-backdrop.component';
import { InternalFooterComponent } from './components/internal-footer/internal-footer.component';
import { SidenavBackdropComponentEssenziell } from "./components/sidenav-backdrop-essenziell/sidenav-backdrop-essenziell.component";
import {CalendarComponent} from "./components/calendar/calendar.component";

@NgModule({
  declarations: [
    CalendarComponent,
    StepperComponent,
    GButtonComponent,
    SidenavBackdropComponent,
    InternalFooterComponent,
    SidenavBackdropComponentEssenziell
    //FieldFormatterPipe,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    MaterialModule,
    CalendarModule,
    TranslateModule.forChild(),
  ],
  exports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    MaterialModule,
    CalendarModule,
    StepperComponent,
    GButtonComponent,
    TranslateModule,
    SidenavBackdropComponent,
    InternalFooterComponent,
    SidenavBackdropComponentEssenziell,
    CalendarComponent
  ],
})
export class SharedModule {}
