/*
00002  * Libavformat API example: Output a media file in any supported
00003  * libavformat format. The default codecs are used.
00004  *
00005  * Copyright (c) 2003 Fabrice Bellard
00006  *
00007  * Permission is hereby granted, free of charge, to any person obtaining a copy
00008  * of this software and associated documentation files (the "Software"), to deal
00009  * in the Software without restriction, including without limitation the rights
00010  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
00011  * copies of the Software, and to permit persons to whom the Software is
00012  * furnished to do so, subject to the following conditions:
00013  *
00014  * The above copyright notice and this permission notice shall be included in
00015  * all copies or substantial portions of the Software.
00016  *
00017  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
00018  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
00019  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
00020  * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
00021  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
00022  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
00023  * THE SOFTWARE.
00024  */
00025 #include <stdlib.h>
00026 #include <stdio.h>
00027 #include <string.h>
00028 #include <math.h>
00029 
00030 #include "libavformat/avformat.h"
00031 #include "libswscale/swscale.h"
00032 
00033 #undef exit
00034 
00035 /* 5 seconds stream duration */
00036 #define STREAM_DURATION   5.0
00037 #define STREAM_FRAME_RATE 25 /* 25 images/s */
00038 #define STREAM_NB_FRAMES  ((int)(STREAM_DURATION * STREAM_FRAME_RATE))
00039 #define STREAM_PIX_FMT PIX_FMT_YUV420P /* default pix_fmt */
00040 
00041 static int sws_flags = SWS_BICUBIC;
00042 
00043 /**************************************************************/
00044 /* audio output */
00045 
00046 float t, tincr, tincr2;
00047 int16_t *samples;
00048 uint8_t *audio_outbuf;
00049 int audio_outbuf_size;
00050 int audio_input_frame_size;
00051 
00052 /*
00053  * add an audio output stream
00054  */
00055 static AVStream *add_audio_stream(AVFormatContext *oc, enum CodecID codec_id)
00056 {
00057     AVCodecContext *c;
00058     AVStream *st;
00059 
00060     st = av_new_stream(oc, 1);
00061     if (!st) {
00062         fprintf(stderr, "Could not alloc stream\n");
00063         exit(1);
00064     }
00065 
00066     c = st->codec;
00067     c->codec_id = codec_id;
00068     c->codec_type = AVMEDIA_TYPE_AUDIO;
00069 
00070     /* put sample parameters */
00071     c->bit_rate = 64000;
00072     c->sample_rate = 44100;
00073     c->channels = 2;
00074 
00075     // some formats want stream headers to be separate
00076     if(oc->oformat->flags & AVFMT_GLOBALHEADER)
00077         c->flags |= CODEC_FLAG_GLOBAL_HEADER;
00078 
00079     return st;
00080 }
00081 
00082 static void open_audio(AVFormatContext *oc, AVStream *st)
00083 {
00084     AVCodecContext *c;
00085     AVCodec *codec;
00086 
00087     c = st->codec;
00088 
00089     /* find the audio encoder */
00090     codec = avcodec_find_encoder(c->codec_id);
00091     if (!codec) {
00092         fprintf(stderr, "codec not found\n");
00093         exit(1);
00094     }
00095 
00096     /* open it */
00097     if (avcodec_open(c, codec) < 0) {
00098         fprintf(stderr, "could not open codec\n");
00099         exit(1);
00100     }
00101 
00102     /* init signal generator */
00103     t = 0;
00104     tincr = 2 * M_PI * 110.0 / c->sample_rate;
00105     /* increment frequency by 110 Hz per second */
00106     tincr2 = 2 * M_PI * 110.0 / c->sample_rate / c->sample_rate;
00107 
00108     audio_outbuf_size = 10000;
00109     audio_outbuf = av_malloc(audio_outbuf_size);
00110 
00111     /* ugly hack for PCM codecs (will be removed ASAP with new PCM
00112        support to compute the input frame size in samples */
00113     if (c->frame_size <= 1) {
00114         audio_input_frame_size = audio_outbuf_size / c->channels;
00115         switch(st->codec->codec_id) {
00116         case CODEC_ID_PCM_S16LE:
00117         case CODEC_ID_PCM_S16BE:
00118         case CODEC_ID_PCM_U16LE:
00119         case CODEC_ID_PCM_U16BE:
00120             audio_input_frame_size >>= 1;
00121             break;
00122         default:
00123             break;
00124         }
00125     } else {
00126         audio_input_frame_size = c->frame_size;
00127     }
00128     samples = av_malloc(audio_input_frame_size * 2 * c->channels);
00129 }
00130 
00131 /* prepare a 16 bit dummy audio frame of 'frame_size' samples and
00132    'nb_channels' channels */
00133 static void get_audio_frame(int16_t *samples, int frame_size, int nb_channels)
00134 {
00135     int j, i, v;
00136     int16_t *q;
00137 
00138     q = samples;
00139     for(j=0;j<frame_size;j++) {
00140         v = (int)(sin(t) * 10000);
00141         for(i = 0; i < nb_channels; i++)
00142             *q++ = v;
00143         t += tincr;
00144         tincr += tincr2;
00145     }
00146 }
00147 
00148 static void write_audio_frame(AVFormatContext *oc, AVStream *st)
00149 {
00150     AVCodecContext *c;
00151     AVPacket pkt;
00152     av_init_packet(&pkt);
00153 
00154     c = st->codec;
00155 
00156     get_audio_frame(samples, audio_input_frame_size, c->channels);
00157 
00158     pkt.size= avcodec_encode_audio(c, audio_outbuf, audio_outbuf_size, samples);
00159 
00160     if (c->coded_frame && c->coded_frame->pts != AV_NOPTS_VALUE)
00161         pkt.pts= av_rescale_q(c->coded_frame->pts, c->time_base, st->time_base);
00162     pkt.flags |= AV_PKT_FLAG_KEY;
00163     pkt.stream_index= st->index;
00164     pkt.data= audio_outbuf;
00165 
00166     /* write the compressed frame in the media file */
00167     if (av_interleaved_write_frame(oc, &pkt) != 0) {
00168         fprintf(stderr, "Error while writing audio frame\n");
00169         exit(1);
00170     }
00171 }
00172 
00173 static void close_audio(AVFormatContext *oc, AVStream *st)
00174 {
00175     avcodec_close(st->codec);
00176 
00177     av_free(samples);
00178     av_free(audio_outbuf);
00179 }
00180 
00181 /**************************************************************/
00182 /* video output */
00183 
00184 AVFrame *picture, *tmp_picture;
00185 uint8_t *video_outbuf;
00186 int frame_count, video_outbuf_size;
00187 
00188 /* add a video output stream */
00189 static AVStream *add_video_stream(AVFormatContext *oc, enum CodecID codec_id)
00190 {
00191     AVCodecContext *c;
00192     AVStream *st;
00193 
00194     st = av_new_stream(oc, 0);
00195     if (!st) {
00196         fprintf(stderr, "Could not alloc stream\n");
00197         exit(1);
00198     }
00199 
00200     c = st->codec;
00201     c->codec_id = codec_id;
00202     c->codec_type = AVMEDIA_TYPE_VIDEO;
00203 
00204     /* put sample parameters */
00205     c->bit_rate = 400000;
00206     /* resolution must be a multiple of two */
00207     c->width = 352;
00208     c->height = 288;
00209     /* time base: this is the fundamental unit of time (in seconds) in terms
00210        of which frame timestamps are represented. for fixed-fps content,
00211        timebase should be 1/framerate and timestamp increments should be
00212        identically 1. */
00213     c->time_base.den = STREAM_FRAME_RATE;
00214     c->time_base.num = 1;
00215     c->gop_size = 12; /* emit one intra frame every twelve frames at most */
00216     c->pix_fmt = STREAM_PIX_FMT;
00217     if (c->codec_id == CODEC_ID_MPEG2VIDEO) {
00218         /* just for testing, we also add B frames */
00219         c->max_b_frames = 2;
00220     }
00221     if (c->codec_id == CODEC_ID_MPEG1VIDEO){
00222         /* Needed to avoid using macroblocks in which some coeffs overflow.
00223            This does not happen with normal video, it just happens here as
00224            the motion of the chroma plane does not match the luma plane. */
00225         c->mb_decision=2;
00226     }
00227     // some formats want stream headers to be separate
00228     if(oc->oformat->flags & AVFMT_GLOBALHEADER)
00229         c->flags |= CODEC_FLAG_GLOBAL_HEADER;
00230 
00231     return st;
00232 }
00233 
00234 static AVFrame *alloc_picture(enum PixelFormat pix_fmt, int width, int height)
00235 {
00236     AVFrame *picture;
00237     uint8_t *picture_buf;
00238     int size;
00239 
00240     picture = avcodec_alloc_frame();
00241     if (!picture)
00242         return NULL;
00243     size = avpicture_get_size(pix_fmt, width, height);
00244     picture_buf = av_malloc(size);
00245     if (!picture_buf) {
00246         av_free(picture);
00247         return NULL;
00248     }
00249     avpicture_fill((AVPicture *)picture, picture_buf,
00250                    pix_fmt, width, height);
00251     return picture;
00252 }
00253 
00254 static void open_video(AVFormatContext *oc, AVStream *st)
00255 {
00256     AVCodec *codec;
00257     AVCodecContext *c;
00258 
00259     c = st->codec;
00260 
00261     /* find the video encoder */
00262     codec = avcodec_find_encoder(c->codec_id);
00263     if (!codec) {
00264         fprintf(stderr, "codec not found\n");
00265         exit(1);
00266     }
00267 
00268     /* open the codec */
00269     if (avcodec_open(c, codec) < 0) {
00270         fprintf(stderr, "could not open codec\n");
00271         exit(1);
00272     }
00273 
00274     video_outbuf = NULL;
00275     if (!(oc->oformat->flags & AVFMT_RAWPICTURE)) {
00276         /* allocate output buffer */
00277         /* XXX: API change will be done */
00278         /* buffers passed into lav* can be allocated any way you prefer,
00279            as long as they're aligned enough for the architecture, and
00280            they're freed appropriately (such as using av_free for buffers
00281            allocated with av_malloc) */
00282         video_outbuf_size = 200000;
00283         video_outbuf = av_malloc(video_outbuf_size);
00284     }
00285 
00286     /* allocate the encoded raw picture */
00287     picture = alloc_picture(c->pix_fmt, c->width, c->height);
00288     if (!picture) {
00289         fprintf(stderr, "Could not allocate picture\n");
00290         exit(1);
00291     }
00292 
00293     /* if the output format is not YUV420P, then a temporary YUV420P
00294        picture is needed too. It is then converted to the required
00295        output format */
00296     tmp_picture = NULL;
00297     if (c->pix_fmt != PIX_FMT_YUV420P) {
00298         tmp_picture = alloc_picture(PIX_FMT_YUV420P, c->width, c->height);
00299         if (!tmp_picture) {
00300             fprintf(stderr, "Could not allocate temporary picture\n");
00301             exit(1);
00302         }
00303     }
00304 }
00305 
00306 /* prepare a dummy image */
00307 static void fill_yuv_image(AVFrame *pict, int frame_index, int width, int height)
00308 {
00309     int x, y, i;
00310 
00311     i = frame_index;
00312 
00313     /* Y */
00314     for(y=0;y<height;y++) {
00315         for(x=0;x<width;x++) {
00316             pict->data[0][y * pict->linesize[0] + x] = x + y + i * 3;
00317         }
00318     }
00319 
00320     /* Cb and Cr */
00321     for(y=0;y<height/2;y++) {
00322         for(x=0;x<width/2;x++) {
00323             pict->data[1][y * pict->linesize[1] + x] = 128 + y + i * 2;
00324             pict->data[2][y * pict->linesize[2] + x] = 64 + x + i * 5;
00325         }
00326     }
00327 }
00328 
00329 static void write_video_frame(AVFormatContext *oc, AVStream *st)
00330 {
00331     int out_size, ret;
00332     AVCodecContext *c;
00333     static struct SwsContext *img_convert_ctx;
00334 
00335     c = st->codec;
00336 
00337     if (frame_count >= STREAM_NB_FRAMES) {
00338         /* no more frame to compress. The codec has a latency of a few
00339            frames if using B frames, so we get the last frames by
00340            passing the same picture again */
00341     } else {
00342         if (c->pix_fmt != PIX_FMT_YUV420P) {
00343             /* as we only generate a YUV420P picture, we must convert it
00344                to the codec pixel format if needed */
00345             if (img_convert_ctx == NULL) {
00346                 img_convert_ctx = sws_getContext(c->width, c->height,
00347                                                  PIX_FMT_YUV420P,
00348                                                  c->width, c->height,
00349                                                  c->pix_fmt,
00350                                                  sws_flags, NULL, NULL, NULL);
00351                 if (img_convert_ctx == NULL) {
00352                     fprintf(stderr, "Cannot initialize the conversion context\n");
00353                     exit(1);
00354                 }
00355             }
00356             fill_yuv_image(tmp_picture, frame_count, c->width, c->height);
00357             sws_scale(img_convert_ctx, tmp_picture->data, tmp_picture->linesize,
00358                       0, c->height, picture->data, picture->linesize);
00359         } else {
00360             fill_yuv_image(picture, frame_count, c->width, c->height);
00361         }
00362     }
00363 
00364 
00365     if (oc->oformat->flags & AVFMT_RAWPICTURE) {
00366         /* raw video case. The API will change slightly in the near
00367            futur for that */
00368         AVPacket pkt;
00369         av_init_packet(&pkt);
00370 
00371         pkt.flags |= AV_PKT_FLAG_KEY;
00372         pkt.stream_index= st->index;
00373         pkt.data= (uint8_t *)picture;
00374         pkt.size= sizeof(AVPicture);
00375 
00376         ret = av_interleaved_write_frame(oc, &pkt);
00377     } else {
00378         /* encode the image */
00379         out_size = avcodec_encode_video(c, video_outbuf, video_outbuf_size, picture);
00380         /* if zero size, it means the image was buffered */
00381         if (out_size > 0) {
00382             AVPacket pkt;
00383             av_init_packet(&pkt);
00384 
00385             if (c->coded_frame->pts != AV_NOPTS_VALUE)
00386                 pkt.pts= av_rescale_q(c->coded_frame->pts, c->time_base, st->time_base);
00387             if(c->coded_frame->key_frame)
00388                 pkt.flags |= AV_PKT_FLAG_KEY;
00389             pkt.stream_index= st->index;
00390             pkt.data= video_outbuf;
00391             pkt.size= out_size;
00392 
00393             /* write the compressed frame in the media file */
00394             ret = av_interleaved_write_frame(oc, &pkt);
00395         } else {
00396             ret = 0;
00397         }
00398     }
00399     if (ret != 0) {
00400         fprintf(stderr, "Error while writing video frame\n");
00401         exit(1);
00402     }
00403     frame_count++;
00404 }
00405 
00406 static void close_video(AVFormatContext *oc, AVStream *st)
00407 {
00408     avcodec_close(st->codec);
00409     av_free(picture->data[0]);
00410     av_free(picture);
00411     if (tmp_picture) {
00412         av_free(tmp_picture->data[0]);
00413         av_free(tmp_picture);
00414     }
00415     av_free(video_outbuf);
00416 }
00417 
00418 /**************************************************************/
00419 /* media file output */
00420 
00421 int main(int argc, char **argv)
00422 {
00423     const char *filename;
00424     AVOutputFormat *fmt;
00425     AVFormatContext *oc;
00426     AVStream *audio_st, *video_st;
00427     double audio_pts, video_pts;
00428     int i;
00429 
00430     /* initialize libavcodec, and register all codecs and formats */
00431     av_register_all();
00432 
00433     if (argc != 2) {
00434         printf("usage: %s output_file\n"
00435                "API example program to output a media file with libavformat.\n"
00436                "The output format is automatically guessed according to the file extension.\n"
00437                "Raw images can also be output by using '%%d' in the filename\n"
00438                "\n", argv[0]);
00439         exit(1);
00440     }
00441 
00442     filename = argv[1];
00443 
00444     /* auto detect the output format from the name. default is
00445        mpeg. */
00446     fmt = av_guess_format(NULL, filename, NULL);
00447     if (!fmt) {
00448         printf("Could not deduce output format from file extension: using MPEG.\n");
00449         fmt = av_guess_format("mpeg", NULL, NULL);
00450     }
00451     if (!fmt) {
00452         fprintf(stderr, "Could not find suitable output format\n");
00453         exit(1);
00454     }
00455 
00456     /* allocate the output media context */
00457     oc = avformat_alloc_context();
00458     if (!oc) {
00459         fprintf(stderr, "Memory error\n");
00460         exit(1);
00461     }
00462     oc->oformat = fmt;
00463     snprintf(oc->filename, sizeof(oc->filename), "%s", filename);
00464 
00465     /* add the audio and video streams using the default format codecs
00466        and initialize the codecs */
00467     video_st = NULL;
00468     audio_st = NULL;
00469     if (fmt->video_codec != CODEC_ID_NONE) {
00470         video_st = add_video_stream(oc, fmt->video_codec);
00471     }
00472     if (fmt->audio_codec != CODEC_ID_NONE) {
00473         audio_st = add_audio_stream(oc, fmt->audio_codec);
00474     }
00475 
00476     /* set the output parameters (must be done even if no
00477        parameters). */
00478     if (av_set_parameters(oc, NULL) < 0) {
00479         fprintf(stderr, "Invalid output format parameters\n");
00480         exit(1);
00481     }
00482 
00483     dump_format(oc, 0, filename, 1);
00484 
00485     /* now that all the parameters are set, we can open the audio and
00486        video codecs and allocate the necessary encode buffers */
00487     if (video_st)
00488         open_video(oc, video_st);
00489     if (audio_st)
00490         open_audio(oc, audio_st);
00491 
00492     /* open the output file, if needed */
00493     if (!(fmt->flags & AVFMT_NOFILE)) {
00494         if (url_fopen(&oc->pb, filename, URL_WRONLY) < 0) {
00495             fprintf(stderr, "Could not open '%s'\n", filename);
00496             exit(1);
00497         }
00498     }
00499 
00500     /* write the stream header, if any */
00501     av_write_header(oc);
00502 
00503     for(;;) {
00504         /* compute current audio and video time */
00505         if (audio_st)
00506             audio_pts = (double)audio_st->pts.val * audio_st->time_base.num / audio_st->time_base.den;
00507         else
00508             audio_pts = 0.0;
00509 
00510         if (video_st)
00511             video_pts = (double)video_st->pts.val * video_st->time_base.num / video_st->time_base.den;
00512         else
00513             video_pts = 0.0;
00514 
00515         if ((!audio_st || audio_pts >= STREAM_DURATION) &&
00516             (!video_st || video_pts >= STREAM_DURATION))
00517             break;
00518 
00519         /* write interleaved audio and video frames */
00520         if (!video_st || (video_st && audio_st && audio_pts < video_pts)) {
00521             write_audio_frame(oc, audio_st);
00522         } else {
00523             write_video_frame(oc, video_st);
00524         }
00525     }
00526 
00527     /* write the trailer, if any.  the trailer must be written
00528      * before you close the CodecContexts open when you wrote the
00529      * header; otherwise write_trailer may try to use memory that
00530      * was freed on av_codec_close() */
00531     av_write_trailer(oc);
00532 
00533     /* close each codec */
00534     if (video_st)
00535         close_video(oc, video_st);
00536     if (audio_st)
00537         close_audio(oc, audio_st);
00538 
00539     /* free the streams */
00540     for(i = 0; i < oc->nb_streams; i++) {
00541         av_freep(&oc->streams[i]->codec);
00542         av_freep(&oc->streams[i]);
00543     }
00544 
00545     if (!(fmt->flags & AVFMT_NOFILE)) {
00546         /* close the output file */
00547         url_fclose(oc->pb);
00548     }
00549 
00550     /* free the stream */
00551     av_free(oc);
00552 
00553     return 0;
00554 }