/*******************************************************************************
 * Copyright (c) 2019 Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is
 *    contributed by Electronic Arts Inc. and may be used for personal,
 *    non-commercial, and academic use only. Redistributions of any program
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in
 *    The Alice 3.0 Art Gallery License.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package org.alice.ide.croquet.models.print;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.svgsupport.BatikSVGDrawer;
import edu.cmu.cs.dennisc.javax.swing.option.Dialogs;
import org.alice.ide.operations.InconsequentialActionOperation;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.DialogTypeSelection;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

abstract class PrintPdfOperation extends InconsequentialActionOperation {
  PrintPdfOperation(UUID uuid) {
    super(uuid);
  }

  @Override
  protected void performInternal() {
    try {
      ByteArrayOutputStream htmlStream = getHtmlToPrint();
      if (htmlStream == null) {
        return;
      }
      ByteArrayOutputStream pdfStream = convertToPdf(htmlStream);
      printPdf(pdfStream);
    } catch (IOException e) {
      notifyWrapAndRethrow(e, "File or HTML generation failed");
    } catch (PrinterException pe) {
      notifyWrapAndRethrow(pe, "Printer failure");
    } catch (Exception e) {
      notifyWrapAndRethrow(e, "Document generation produced an error");
    }
  }

  private void notifyWrapAndRethrow(Exception e, String message) {
    notifyUserOfProblem(message);
    e.printStackTrace();
    throw new RuntimeException(message, e);
  }

  void notifyUserOfProblem(String message) {
    Dialogs.showInfo("Unable to Print", message);
  }

  protected abstract ByteArrayOutputStream getHtmlToPrint() throws IOException;

  private ByteArrayOutputStream convertToPdf(ByteArrayOutputStream htmlStream) throws Exception {
    ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
    PdfRendererBuilder builder = new PdfRendererBuilder();
    builder.useFastMode();
    builder.useSVGDrawer(new BatikSVGDrawer());
    builder.withHtmlContent(htmlStream.toString(), "");
    builder.toStream(pdfStream);
    builder.run();
    return pdfStream;
  }

  private void printPdf(ByteArrayOutputStream pdfStream) throws IOException, PrinterException {
    PrinterJob job = PrinterJob.getPrinterJob();
    try (PDDocument document = PDDocument.load(pdfStream.toByteArray())) {
      job.setPageable(new PDFPageable(document));

      PrintRequestAttributeSet printOptions = new HashPrintRequestAttributeSet();
      printOptions.add(DialogTypeSelection.NATIVE);
      if (job.printDialog(printOptions)) {
        job.print(printOptions);
      }
    }
  }
}
