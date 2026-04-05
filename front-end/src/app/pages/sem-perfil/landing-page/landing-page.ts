import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DividerModule } from 'primeng/divider';
import { TagModule } from 'primeng/tag';

type FeatureItem = {
  icon: string;
  title: string;
  text: string;
};

type TestimonialItem = {
  name: string;
  role: string;
  text: string;
};

@Component({
  selector: 'app-landing-page',
  imports: [ButtonModule, CardModule, DividerModule, TagModule],
  templateUrl: './landing-page.html',
  styleUrl: './landing-page.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LandingPage {
  private readonly router = inject(Router);

  protected readonly features: FeatureItem[] = [
    {
      icon: 'pi pi-wallet',
      title: 'Conta Inteligente',
      text: 'Controle entradas, saídas e objetivos em um só lugar, com visão clara da sua vida financeira.',
    },
    {
      icon: 'pi pi-bolt',
      title: 'Transferências Instantâneas',
      text: 'Envie e receba dinheiro em segundos, a qualquer hora, com praticidade e confirmação em tempo real.',
    },
    {
      icon: 'pi pi-shield',
      title: 'Segurança em Camadas',
      text: 'Sua conta protegida com autenticação, monitoramento contínuo e tecnologia contra fraudes.',
    },
  ];

  protected readonly testimonials: TestimonialItem[] = [
    {
      name: 'Mariana Costa',
      role: 'Freelancer',
      text: 'Com o Bantads, consigo separar despesas pessoais e profissionais sem complicação.',
    },
    {
      name: 'Patrícia Lima',
      role: 'Empreendedora',
      text: 'As transferências instantâneas agilizaram meu negócio e melhoraram meu fluxo de caixa.',
    },
    {
      name: 'Rafael Mendes',
      role: 'Analista',
      text: 'O app é rápido, seguro e me dá a confiança de resolver tudo pelo celular.',
    },
  ];

  protected navigateToLogin(): void {
    void this.router.navigate(['/login']);
  }

  protected navigateToRegister(): void {
    void this.router.navigate(['/autocadastro']);
  }
}
